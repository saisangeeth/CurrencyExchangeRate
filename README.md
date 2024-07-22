Open the application in intellij and i have used jdk 17 version to run the application.
Do clean build.
Run the command mvn spring-boot:run .
After the application starts, in chrome window, use the given link to export the currency exchange rate in csv or json or pdf 
PDF: http://localhost:8000/api/exchange-rates/export?startDate=2024-06-10&endDate=2024-07-20&format=pdf
CSV: http://localhost:8000/api/exchange-rates/export?startDate=2024-06-10&endDate=2024-07-20&format=csv
JSON: http://localhost:8000/api/exchange-rates/export?startDate=2024-06-10&endDate=2024-07-20&format=json

Note: In the above URL, the endDate should always be current/today’s date

For calculating currency exchange rate use below link:
 http://localhost:8000/api/exchange-rates/calculate?currencyA=EUR&currencyB=GBP
