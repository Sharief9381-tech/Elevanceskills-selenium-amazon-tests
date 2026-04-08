# Amazon Selenium Testing — ElevanceSkills Internship

Selenium automation project built on the training project provided by ElevanceSkills.
All 6 internship tasks are implemented as extra test cases inside the `training_project/` folder.

---

## Tech Stack

- Java 17
- Selenium WebDriver 4.24.0
- TestNG 7.10.2
- Maven
- Page Object Model (POM)
- Jakarta Mail (email alerts)

---

## Project Structure

```
├── training_project/                        ← Main submission (built on training code)
│   ├── src/main/java/com/ama/qa/
│   │   ├── base/TestBase.java               ← WebDriver setup
│   │   ├── config/config.properties         ← URL, credentials
│   │   ├── pages/                           ← Page Object classes
│   │   │   ├── AmazonHomePage.java
│   │   │   ├── AmazonLoginPage.java
│   │   │   ├── AmazonSearchProductsPage.java
│   │   │   ├── AmazonProductDetailsPage.java
│   │   │   ├── AmazonProductCheckoutPage.java
│   │   │   └── ShoppingCartPage.java
│   │   └── util/ConfigReader.java
│   └── src/test/java/testcases/
│       ├── AmazonHomeTest.java              ← Original training tests
│       ├── AmazonLoginTest.java
│       ├── AmazonSearchProductsTest.java
│       ├── AmazonProductDetailsTest.java
│       ├── AmazonProductCheckoutTest.java
│       ├── ShoppingCartTest.java
│       ├── Task1_ProductSelectionTest.java  ← Internship Task 1
│       ├── Task2_SearchFiltersTest.java     ← Internship Task 2
│       ├── Task3_EndToEndPurchaseTest.java  ← Internship Task 3
│       ├── Task4_LoginProfileTest.java      ← Internship Task 4
│       ├── Task5_CartTotalTest.java         ← Internship Task 5
│       └── Task6_PriceMonitorTest.java      ← Internship Task 6
│
├── Task1_ProductSelection/                  ← Standalone Task 1 project
├── Task2_ProductFilter/                     ← Standalone Task 2 project
├── Task3_EndToEnd/                          ← Standalone Task 3 project
├── Task4_LoginProfile/                      ← Standalone Task 4 project
├── Task5_CartTotal/                         ← Standalone Task 5 project
└── Task6_PriceMonitor/                      ← Standalone Task 6 project
```

---

## Internship Tasks

### Task 1 — Product Selection (3 PM – 6 PM)
`Task1_ProductSelectionTest.java`
- Searches Amazon for a product
- Skips products starting with A, B, C, or D
- Skips electronics products
- Verifies title, price, and availability on the product page

### Task 2 — Search Filters (3 PM – 6 PM)
`Task2_SearchFiltersTest.java`
- Searches for "Crocs shoes"
- Applies brand filter (brand starts with C)
- Validates price > Rs 2000 and rating > 4 stars for each result

### Task 3 — End-to-End Purchase Flow (6 PM – 7 PM)
`Task3_EndToEndPurchaseTest.java`
- Searches for a product
- Adds it to cart
- Proceeds to checkout
- Verifies cart total > Rs 500

### Task 4 — Login and Profile Validation (12 PM – 3 PM)
`Task4_LoginProfileTest.java`
- Logs into Amazon
- Handles Two-Step Verification (OTP) manually
- Extracts the display name from the nav bar
- Validates username does NOT contain: A, C, G, I, L, K

### Task 5 — Multi-Product Cart (6 PM – 7 PM)
`Task5_CartTotalTest.java`
- Adds multiple products to cart
- Verifies cart total > Rs 2000
- Validates username is exactly 10 alphanumeric characters

### Task 6 — Price Monitor with Email Alert
`Task6_PriceMonitorTest.java`
- Scrapes live product price from Amazon using Selenium
- Compares with a threshold (Rs 1500)
- Sends Gmail alert if price drops below threshold
- Saves price history to `price_history.csv`
- Uses `ScheduledExecutorService` for periodic checks

---

## Setup

### 1. Clone the repo
```bash
git clone https://github.com/Sharief9381-tech/Elevanceskills-selenium-amazon-tests.git
cd Elevanceskills-selenium-amazon-tests/training_project
```

### 2. Update credentials
Edit `src/main/java/com/ama/qa/config/config.properties`:
```properties
URL      = https://www.amazon.in/
USERNAME = your_amazon_email@gmail.com
PASSWORD = your_amazon_password
BROWSER  = chrome
```

### 3. Run all tests
```bash
mvn test
```

### 4. Run a specific task
```bash
mvn test -Dtest=Task1_ProductSelectionTest -DFORCE_RUN=true
mvn test -Dtest=Task2_SearchFiltersTest    -DFORCE_RUN=true
mvn test -Dtest=Task3_EndToEndPurchaseTest -DFORCE_RUN=true
mvn test -Dtest=Task4_LoginProfileTest     -DFORCE_RUN=true
mvn test -Dtest=Task5_CartTotalTest        -DFORCE_RUN=true
mvn test -Dtest=Task6_PriceMonitorTest
```

> `-DFORCE_RUN=true` bypasses the time window restriction for testing.
> Remove it for real execution — tests will only run in their allowed time windows.

---

## Time Windows

| Task | Allowed Time |
|------|-------------|
| Task 1 | 3 PM – 6 PM |
| Task 2 | 3 PM – 6 PM |
| Task 3 | 6 PM – 7 PM |
| Task 4 | 12 PM – 3 PM |
| Task 5 | 6 PM – 7 PM |
| Task 6 | Any time |

---

## Author
**Shaik Sharief**
ElevanceSkills Selenium Internship
