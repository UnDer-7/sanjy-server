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
	awk 'BEGIN {first=1} /^ *=====/ { if (!first) print ""; print "   " $$0; first=0; next } { print "   " $$0 }'

# Hidden
.PHONY: all
all: help





# ==================================================================================== #
## ===== COMPILE =====
# ==================================================================================== #
## compile: Just compile the application
.PHONY: compile
compile:
	@echo ">>> Compiling…"
	./mvnw clean compile





# ==================================================================================== #
## ===== TEST =====
# ==================================================================================== #
## test: Run all the application test
.PHONY: test
test:
	@echo ">>> Running all tests…"
	./mvnw clean compile test





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
## build/jvm: Build the project to be run on a JVM environment
.PHONY: build/jvm
build/jvm:
	@START=$$(date +%s); \
	echo 'Installing all modules...'; \
	./mvnw clean install -DskipTests -B; \
	echo 'Building for JVM...'; \
	./mvnw clean package -B -Dmaven.test.skip -T1C -DargLine="Xms2g -Xmx2g" --batch-mode -q; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "JVM build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/jvm/docker: Build a Docker image with jvm
.PHONY: build/jvm/docker
build/jvm/docker:
	@START=$$(date +%s); \
	echo 'Building docker image for JVM...'; \
	docker build --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker JVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/jvm/docker/force: Build a Docker image with jvm without caching layers
.PHONY: build/jvm/docker/force
build/jvm/docker/force:
	@START=$$(date +%s); \
	echo 'Building docker image for JVM without caching layers'; \
	docker build --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker JVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm: Build an executable to be run without JVM
.PHONY: build/graalvm
build/graalvm:
	@START=$$(date +%s) && \
	echo 'Loading environment variables from .env...' && \
	set -a && \
	. $(CURDIR)/.env && \
	set +a && \
	echo 'Installing all modules...' && \
	./mvnw clean install -DskipTests -B && \
	echo 'Building GraalVM native image...' && \
	./mvnw -Pnative -Dmaven.test.skip -B -pl infrastructure native:compile && \
	END=$$(date +%s) && \
	ELAPSED=$$((END-START)) && \
	echo "GraalVM build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm/docker: Build a Docker image with GraalVM
.PHONY: build/graalvm/docker
build/graalvm/docker:
	@START=$$(date +%s); \
	echo 'Building docker image for GraalVM...'; \
	docker build --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker GraalVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm/docker/force: Build a Docker image with GraalVM without caching layers
.PHONY: build/graalvm/docker/force
build/graalvm/docker/force:
	@START=$$(date +%s); \
	echo 'Building docker image for GraalVM without caching layers'; \
	docker build --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker GraalVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"





# ==================================================================================== #
## ===== CODING STYLE =====
# ==================================================================================== #
## fmt: Format all source code files using Spotless
.PHONY: fmt
fmt:
	@echo ">>> Formatting all source code files…"
	./mvnw clean spotless:apply -B -ntp

## fmt/check: Check code formatting without applying changes
.PHONY: fmt/check
fmt/check:
	@echo ">>> Checking code formatting…"
	./mvnw clena spotless:check -B -ntp

## lint: Verify code compliance with Checkstyle standards
.PHONY: lint
lint:
	@echo ">>> Running Checkstyle validation…"
	@./mvnw clean checkstyle:check -B -ntp || (echo "" && \
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
	@./mvnw clean checkstyle:checkstyle-aggregate -B -ntp && (echo "" && \
	echo "==========================================================================" && \
	echo " ✅  CHECKSTYLE REPORT GENERATED SUCCESSFULLY  ✅" && \
	echo "==========================================================================" && \
	echo "" && \
	echo "Report location: target/site/checkstyle-aggregate.html" && \
	echo "" && \
	echo "Simply open it in your browser to view detailed violation information." && \
	echo "==========================================================================" && \
	echo "")
