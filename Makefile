# ==================================================================================== #
# VARIABLES
# ==================================================================================== #
PROJECT_NAME=sanjy-server
REGISTRY_HOST=under7
POM_VERSION := $(shell ./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)





# ==================================================================================== #
## ===== HELPERS =====
# ==================================================================================== #
## help: Describe all available targets
.PHONY: help
help:
	@echo 'Usage: make <target>'
	@sed -n 's/^##//p' $(MAKEFILE_LIST) | column -t -s ':' | \
	awk 'BEGIN {first=1} \
						/^ *=====/ { if (!first) print ""; print "   " $$0; first=0; next } \
						/^ *-----/ { print ""; print "   " $$0; next } \
						{ print "   " $$0 }'

# Hidden
.PHONY: all
all: help





# ==================================================================================== #
## ===== DEV =====
# ==================================================================================== #
## dev/compile: Just compile the application
.PHONY: dev/compile
dev/compile:
	@echo ">>> Compiling…"
	./mvnw -B -ntp clean compile

## dev/run: Clean, compile, and run the application locally with spring-boot:run (loads .env variables)
.PHONY: dev/run
dev/run:
	@echo ">>> Loading .env, compiling, and starting application…"
	@set -a && \
	eval $$(grep -v '^\s*#' .env | grep -v '^\s*$$' | sed 's/\r$$//') && \
	set +a && \
	./mvnw -B -ntp clean compile spring-boot:run -pl infrastructure





# ==================================================================================== #
## ===== TEST =====
# ==================================================================================== #
## test: Run all tests (unit tests ending with *Test.java + integration tests ending with *IT.java) in JVM mode
.PHONY: test
test:
	echo ">>> Running all tests (unit + integration)…" && \
	./mvnw -B -ntp clean compile verify

## test/native: Run integration tests only (*IT.java) in GraalVM native mode. Unit tests (*Test.java) are excluded because Mockito is incompatible with GraalVM Native Image
.PHONY: test/native
test/native:
	echo ">>> Running integration tests only (unit tests excluded due to Mockito incompatibility)…" && \
	./mvnw -B -ntp -PnativeTest clean compile test




# ==================================================================================== #
## ===== DATABASE =====
# ==================================================================================== #
## db/clean: Clean all database data and reset sequences
.PHONY: db/clean
db/clean:
	@echo ">>> Cleaning database and resetting sequences…"
	@docker exec -i sanJy_database psql -U admin_usr -d diet_control < local/clean-db.sql
	@echo ">>> Database cleaned successfully!"

## db/seed: Populate database with sample data
.PHONY: db/seed
db/seed:
	@echo ">>> Populating database with sample data…"
	@docker exec -i sanJy_database psql -U admin_usr -d diet_control < local/sample-data.sql
	@echo ">>> Database populated successfully!"





# ==================================================================================== #
## ===== BUILD =====
# ==================================================================================== #
## ----- JVM -----
## build/jvm: Build the project to be run on a JVM environment
.PHONY: build/jvm
build/jvm:
	@START=$$(date +%s); \
	echo 'Installing all modules...'; \
	./mvnw -B -ntp clean install -DskipTests; \
	echo 'Building for JVM...'; \
	./mvnw -B -ntp clean package -Dmaven.test.skip -T1C -DargLine="Xms2g -Xmx2g" --batch-mode -q; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "JVM build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/jvm/docker: Build a Docker image with jvm (full build from scratch)
.PHONY: build/jvm/docker
build/jvm/docker:
	@START=$$(date +%s); \
	echo 'Building docker image for JVM (full mode)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker JVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/jvm/docker/local: Build a Docker image with jvm using pre-built artifacts (fast)
.PHONY: build/jvm/docker/local
build/jvm/docker/local:
	@START=$$(date +%s); \
	echo 'Building docker image for JVM (local mode - using pre-built JAR)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=local --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker JVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/jvm/docker/force: Build a Docker image with jvm without caching layers (For debugging)
.PHONY: build/jvm/docker/force
build/jvm/docker/force:
	@START=$$(date +%s); \
	echo 'Building docker image for JVM without caching layers'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker JVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## ----- GRAALVM -----
## build/graalvm: Build an executable to be run without JVM
.PHONY: build/graalvm
build/graalvm:
	@START=$$(date +%s) && \
	echo 'Loading environment variables from .env...' && \
	set -a && \
	. $(CURDIR)/.env && \
	set +a && \
	echo 'Installing all modules...' && \
	./mvnw -B -ntp clean install -DskipTests && \
	echo 'Building GraalVM native image...' && \
	./mvnw -B -ntp -Pnative -Dmaven.test.skip -pl infrastructure clean native:compile && \
	END=$$(date +%s) && \
	ELAPSED=$$((END-START)) && \
	echo "GraalVM build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm/docker: Build a Docker image with GraalVM (full build from scratch)
.PHONY: build/graalvm/docker
build/graalvm/docker:
	@START=$$(date +%s); \
	echo 'Building docker image for GraalVM (full mode)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker GraalVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm/docker/local: Build a Docker image with GraalVM using pre-built artifacts (fast)
.PHONY: build/graalvm/docker/local
build/graalvm/docker/local:
	@START=$$(date +%s); \
	echo 'Building docker image for GraalVM (local mode - using pre-built native binary)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=local --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker GraalVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm/docker/force: Build a Docker image with GraalVM without caching layers (For debugging)
.PHONY: build/graalvm/docker/force
build/graalvm/docker/force:
	@START=$$(date +%s); \
	echo 'Building docker image for GraalVM without caching layers'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker GraalVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"





# ==================================================================================== #
## ===== QUALITY =====
# ==================================================================================== #
## snyk/test: Scan for vulnerabilities in dependencies and code (requires SNYK_TOKEN env var)
.PHONY: snyk/test
snyk/test:
	@echo ">>> Running Snyk vulnerability scan..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	./mvnw -B -ntp snyk:test
	@echo ">>> Snyk scan completed!"

## snyk/monitor: Upload project snapshot to Snyk for continuous monitoring (requires SNYK_TOKEN env var)
.PHONY: snyk/monitor
snyk/monitor:
	@echo ">>> Uploading project snapshot to Snyk..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	./mvnw -B -ntp snyk:monitor
	@echo ">>> Project snapshot uploaded successfully!"
	@echo ">>> View results in your Snyk dashboard"

## sonar: Publish analysis results to SonarCloud (run 'make test' first, requires SONAR_TOKEN env var)
.PHONY: sonar
sonar:
	@echo ">>> Publishing analysis to SonarCloud..."
	@if [ -z "$$SONAR_TOKEN" ]; then \
		echo "ERROR: SONAR_TOKEN environment variable is not set"; \
		echo "Please set it with: export SONAR_TOKEN=your_token_here"; \
		exit 1; \
	fi
	@if [ ! -f "aggregate-report/target/site/jacoco-aggregate/jacoco.xml" ]; then \
		echo "ERROR: JaCoCo coverage report not found"; \
		echo "Please run 'make test' first to generate the coverage report"; \
		exit 1; \
	fi
	./mvnw -B -ntp sonar:sonar
	@echo ">>> Analysis published successfully!"
	@echo ">>> View results at: https://sonarcloud.io/dashboard?id=UnDer-7_sanjy-server"





# ==================================================================================== #
## ===== VERSION =====
# ==================================================================================== #
## version: Display current project version from pom.xml
.PHONY: version
version:
	@echo "$(POM_VERSION)"

## version/set: Set new project version (usage; make version/set 1.0.23)
.PHONY: version/set
version/set:
	@if [ -z "$(filter-out version/set,$(MAKECMDGOALS))" ]; then \
		echo "ERROR: Version number is required"; \
		echo "Usage: make version/set 1.0.23"; \
		exit 1; \
	fi
	@echo ">>> Setting project version to $(filter-out version/set,$(MAKECMDGOALS))..."
	@./mvnw -B -ntp versions:set -DnewVersion=$(filter-out version/set,$(MAKECMDGOALS)) -DgenerateBackupPoms=false
	@echo ">>> Project version updated to $(filter-out version/set,$(MAKECMDGOALS))"

# Prevent make from treating version number as a target
%:
	@:




# ==================================================================================== #
## ===== CODING STYLE =====
# ==================================================================================== #
## fmt: Format all source code files using Spotless
.PHONY: fmt
fmt:
	@echo ">>> Formatting all source code files…"
	./mvnw -B -ntp clean spotless:apply

## fmt/check: Check code formatting without applying changes
.PHONY: fmt/check
fmt/check:
	@echo ">>> Checking code formatting…"
	./mvnw -B -ntp clean spotless:check

## lint: Verify code compliance with Checkstyle standards
.PHONY: lint
lint:
	@echo ">>> Running Checkstyle validation…"
	@./mvnw -B -ntp clean checkstyle:check || (echo "" && \
	echo "==========================================================================" && \
	echo " ⚠️  CODE STYLE VIOLATIONS DETECTED  ⚠️" && \
	echo "==========================================================================" && \
	echo "" && \
	echo "The code does not comply with the project's coding standards." && \
	echo "" && \
	echo "To see a detailed HTML report with specific violations, run:" && \
	echo "    make lint/report" && \
	echo "" && \
	echo "The report makes it much easier to identify and fix the issues." && \
	echo "==========================================================================" && \
	echo "" && exit 1)

## lint/report: Generate detailed HTML report of Checkstyle violations
.PHONY: lint/report
lint/report:
	@echo ">>> Generating Checkstyle HTML report…"
	@./mvnw -B -ntp clean checkstyle:checkstyle-aggregate && (echo "" && \
	echo "==========================================================================" && \
	echo " ✅  CHECKSTYLE REPORT GENERATED SUCCESSFULLY  ✅" && \
	echo "==========================================================================" && \
	echo "" && \
	echo "Report location: target/site/checkstyle-aggregate.html" && \
	echo "" && \
	echo "Simply open it in your browser to view detailed violation information." && \
	echo "==========================================================================" && \
	echo "")
