.DEFAULT_GOAL := build

build:
	./mvnw clean package

update:
	./mvnw versions:update-properties versions:display-plugin-updates
