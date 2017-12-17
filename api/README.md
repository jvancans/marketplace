## Running the app (multiple options)
- Using bash and gradle wrapper: ```./gradlew bootRun```
- Using gradle: ```gradle bootRun``` (requires gradle)
- Using IDE: Run the main class ```MarketplaceAPI``` as Java/Spring Boot application

## Running tests
- Using gradle: ```gradle test```
- Report can be found here: ```build/reports/tests/test/index.html```

## Endpoints
- localhost:8080/trades GET
- localhost:8080/trades POST
- see ```TradeResource``` and ```MarketplaceAPIAcceptanceTest``` for examples