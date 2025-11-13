# ==================================================================================== #
# VARIABLES
# ==================================================================================== #
PROJECT_NAME=sanjy-server
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
## seed-db: Populate database with sample data
.PHONY: seed-db
seed-db:
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

## build/graalvm: Build an executable to be run without JVM
.PHONY: build/graalvm
build/graalvm:
	@START=$$(date +%s); \
	echo 'Loading environment variables from .env...'; \
	set -a; \
	. $(CURDIR)/.env; \
	set +a; \
	echo 'Installing all modules...'; \
	./mvnw clean install -DskipTests -B; \
	echo 'Building GraalVM native image...'; \
	./mvnw -Pnative -Dmaven.test.skip -B -pl infrastructure native:compile; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "GraalVM build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/jvm/docker: Build a Docker image with jvm
.PHONY: build/jvm/docker
build/jvm/docker:
	@START=$$(date +%s); \
	echo 'Building docker image for JVM...'; \
	docker build --tag 'Local/$(PROJECT_NAME)-jvm:$(POM_VERSION)' -f Dockerfile_jvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker JVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/jvm/docker/force: Build a Docker image with jvm without caching layers
.PHONY: build/jvm/docker/force
build/jvm/docker/force:
	@START=$$(date +%s); \
	echo 'Building docker image for JVM without caching layers'; \
	docker build --tag 'Local/$(PROJECT_NAME)-jvm:$(POM_VERSION)' -f Dockerfile_jvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker JVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm/docker: Build a Docker image with GraalVM
.PHONY: build/graalvm/docker
build/graalvm/docker:
	@START=$$(date +%s); \
	echo 'Building docker image for GraalVM...'; \
	docker build --tag 'Local/$(PROJECT_NAME)-graalvm:$(POM_VERSION)' -f Dockerfile_graalvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker GraalVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/graalvm/docker/force: Build a Docker image with GraalVM without caching layers
.PHONY: build/graalvm/docker/force
build/graalvm/docker/force:
	@START=$$(date +%s); \
	echo 'Building docker image for GraalVM without caching layers'; \
	docker build --tag 'Local/$(PROJECT_NAME)-graalvm:$(POM_VERSION)' -f Dockerfile_graalvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo "Docker GraalVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"
