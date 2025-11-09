# ==================================================================================== #
# VARIABLES
# ==================================================================================== #




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
