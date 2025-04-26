
# 📈 StockMonitor Application

StockMonitor is a Spring Boot application that allows users to manage their stock portfolios, upload stocks, receive real-time email updates.
It also includes JWT-based secure authentication with OTP verification during registration.

---

## ⚙️ Features

- User Registration with OTP Email Verification.
- Secure Login with JWT Tokens.
- Upload and Manage Stock Folios.
- Receive Folio Value Reports via Email.
- Get real-time stocks prices.

---

## 📚 API Documentation

| Endpoint             | Method | Description                               | Request Body / Params                           | Response              |
|----------------------|--------|-------------------------------------------|-------------------------------------------------|-----------------------|
| `/api/auth/register` | POST   | Register a new user and send OTP email     | `{ "email": "example@mail.com", "password": "pass" }` | `"OTP sent to email"` |
| `/api/auth/verify`   | POST   | Verify account using email and OTP         | `{ "email": "example@mail.com", "otp": "123456" }` | `"Account verified"` |
| `/api/auth/login`    | POST   | Login after OTP verification, get JWT      | `{ "email": "example@mail.com", "password": "pass" }` | `"jwt-token-string"`  |
| `/api/folio/upload`  | POST   | Add a new stock to user’s folio manually    | `{ "ticker": "AAPL", "quantity": 10 }` | `"Stock added successfully"` |
| `/api/folio/value`   | GET    | Fetch current folio details and total value | - | Folio details |
| `/api/folio/email`   | POST   | Send folio summary email                   | - | `"Folio emailed to your email"` |

---

✅ **Notes:**
- Register and verify your account before login.
- JWT token must be included in the Authorization header (`Bearer token`) for all `/api/folio/**`.

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot 3
- Spring Security + JWT Authentication
- MySQL Database
- JavaMailSender for Email
- Twelve Data API (for live stock prices)

---

## 📦 How to Run

1. Clone the repository
2. Set environment variables (`MAIL`, `MAIL_PASSWORD`, `TWELVE_API_KEY`, etc.)
3. Configure application.properties for your database
4. Run the application using Maven or your IDE
5. Access APIs via Postman.

---

Happy Monitoring! 🚀
