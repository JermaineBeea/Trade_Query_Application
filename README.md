# Trade Dynamics Web Application

A comprehensive Java-based web application for analyzing trade profitability across multi-commodity conversions. This application implements the mathematical framework described in the Trade Dynamics documentation to calculate returns, profit factors, and optimal trade parameters.

## 📋 Overview

This application provides both computational backend and web-based interface for analyzing trades involving:
- Primary to secondary commodity conversions
- Multi-commodity trading chains
- Market rate vs. execution rate calculations
- Spread impact analysis
- Real-time trade parameter optimization

## 🎯 Key Features

- **Dual Calculation Modes**:
  - Execution-based (using sell/buy execution rates)
  - Market-based (using market rates with spread adjustments)

- **Interactive Web Interface**:
  - Real-time parameter updates
  - Dynamic calculations for 5 trade variables
  - Example data loading for testing
  - Reset functionality

- **Database Persistence**:
  - SQLite backend for storing trade parameters
  - SQL export capability
  - Zero-initialization on startup

- **Comprehensive Testing**:
  - JUnit test suite for all trade functions
  - Tolerance-based assertions for decimal precision

## 🏗️ Architecture

```
src/
├── main/java/co/za/Main/
│   ├── TradeModules/
│   │   └── TradeFunction.java          # Core calculation engine
│   ├── WebTradeApplication/
│   │   ├── WebApp.java                 # Application entry point
│   │   ├── WebServerApplication.java   # HTTP server & API endpoints
│   │   ├── WebAppDataBase.java         # SQLite database operations
│   │   └── WebQueryImplementation.java # Business logic layer
│   └── ConsoleApplication/             # (Optional console interface)
└── test/java/co/za/MainTest/
    └── TestTradeFunctions.java         # Unit tests

trade-index.html                        # Web interface
Documentation/README.md                 # Mathematical framework
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.x
- Modern web browser

### Installation

1. **Clone the repository**:
```bash
git clone <repository-url>
cd WebTradeQueryApp
```

2. **Set Java path** (Linux/macOS):
```bash
source setPath.sh
```

3. **Build the project**:
```bash
mvn clean compile
```

4. **Run tests** (optional):
```bash
mvn test
```

### Running the Application

**Start the web server**:
```bash
mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"
```

**Or use VS Code launch configuration**:
- Open in VS Code
- Press F5 and select "WebApp"

**Access the interface**:
- Open browser to `http://localhost:8080`

## 📊 Using the Application

### Trade Variables

The application calculates relationships between five key variables:

| Variable | Description |
|----------|-------------|
| **Trade Profit** | Net return in profit currency |
| **Profit Factor** | Ratio-based profitability metric |
| **Trade Amount** | Quantity traded in intermediate currency |
| **Buy Variable** | Buy execution or market rate |
| **Sell Variable** | Sell execution or market rate |

### Calculation Parameters

- **Spread**: Difference between buy and sell rates (e.g., 0.01)
- **Rate AP**: Conversion rate from intermediate to primary commodity
- **Rate PN**: Conversion rate from primary to profit currency
- **Based on Market Rate**: Toggle between market-based and execution-based calculations

### Workflow

1. **Initialize Values**:
   - Click "Load Example Values" for demo data
   - Or manually enter minimum/maximum values for any variables

2. **Configure Parameters**:
   - Set spread, Rate AP, and Rate PN
   - Toggle calculation mode if needed

3. **Run Calculations**:
   - Click "Run Calculations"
   - View calculated return min/max values

4. **Reset**:
   - "Reset to Zero" clears all values

## 🧮 Mathematical Framework

### Core Formula

```
Trade Profit = Trade Amount × Rate_AP × Rate_PN × Profit Factor
```

Where:
```
Profit Factor = (Sell Rate / Buy Rate) - 1
```

### Execution vs. Market Mode

**Execution-based** (default):
- Uses direct sell/buy execution rates
- `Profit Factor = (sellVariable / buyVariable) - 1`

**Market-based**:
- Converts market rates to execution rates
- `Sell Execution = Market Rate - (Spread / 2)`
- `Buy Execution = Market Rate + (Spread / 2)`

See [Documentation/README.md](Documentation/README.md) for complete mathematical derivations.

## 🗄️ Database Schema

```sql
CREATE TABLE WebAppDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
)
```

- **minimum/maximum**: Input values (user-editable)
- **returnmin/returnmax**: Calculated output values

## 🔌 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Serve web interface |
| `/api/data` | GET | Retrieve all trade variables |
| `/api/update` | POST | Update variable value |
| `/api/query` | POST | Run calculations with parameters |
| `/api/reset` | POST | Reset values to zero |

### Example API Call

```bash
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "spread": 0.01,
    "rateKA": 1.0,
    "ratePN": 18.5571,
    "basedOnMarketRate": false
  }'
```

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

Tests cover:
- Return profit calculations
- Profit factor derivations
- Trade amount inversions
- Sell/buy variable solving
- Mode toggle functionality

## 📝 Configuration

Edit `trade-index.html` to modify:

```javascript
const APP_DEFAULTS = {
    spread: 0.01,
    rateKA: 1,
    ratePN: 18.5571,
    basedOnMarketRate: false
};

const PROMPT_TO_RESET_TO_ZERO = true;
const PROMPT_USER_ON_LOAD_EXAMPLES = true;
```

## 🛠️ Development

### Build with Maven

```bash
# Compile
mvn compile

# Run tests
mvn test

# Package
mvn package
```

### VS Code Launch Configurations

Available in `.vscode/launch.json`:
- **WebApp**: Start web server
- **ConsoleImplementation**: Run console version
- **Current File**: Debug current Java file

## 📦 Dependencies

```xml
<dependencies>
  <!-- JUnit 5 for testing -->
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.2</version>
  </dependency>
  
  <!-- SQLite JDBC driver -->
  <dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.3.0</version>
  </dependency>
</dependencies>
```

## 📄 License

This project is provided as-is for educational and commercial use.

## 👤 Author

**Tebagano Beea**  
Date: September 09, 2025

## 🤝 Contributing

Contributions are welcome! Please ensure:
- All tests pass (`mvn test`)
- Code follows existing patterns
- Mathematical accuracy is maintained

## 📞 Support

For issues or questions:
1. Check the mathematical framework in `Documentation/README.md`
2. Review test cases in `TestTradeFunctions.java`
3. Examine console output for detailed calculation logs

---

**Note**: The application starts with zero-initialized values. Use "Load Example Values" button for testing or manually input your trade parameters.