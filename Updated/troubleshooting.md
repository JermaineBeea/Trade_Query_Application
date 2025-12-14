# Troubleshooting Guide

## Web Page Shows No Data / No Columns

### Problem 1: Database Not Initialized
**Symptoms:** Empty table, no rows showing

**Solution:**
```bash
# Delete the database and restart
rm WebAppDataBase.db WebAppDataBase.sql
mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"
```

The application will create a fresh database with the correct schema on startup.

### Problem 2: Old Variable Names in Database
**Symptoms:** API errors, "Variable not found" in console

**Solution:** The updated `WebAppDataBase.java` now includes automatic migration from old names:
- `buyvariable` → `closingvalue`
- `sellvariable` → `openingvalue`

Simply restart the application - it will detect and migrate automatically.

### Problem 3: Browser Cache
**Symptoms:** Old HTML showing, buttons not working

**Solution:**
```
1. Open browser DevTools (F12)
2. Right-click the reload button
3. Select "Empty Cache and Hard Reload"
```

Or use Ctrl+Shift+R (Windows/Linux) or Cmd+Shift+R (Mac)

### Problem 4: API Not Responding
**Symptoms:** Loading forever, no data appears

**Check console logs:**
```bash
# In the terminal running the server, you should see:
Trade Web Server running at: http://localhost:8080
Table is empty - populating with zero values...
Initialized tradeprofit with all zero values
Initialized profitfactor with all zero values
Initialized tradeamount with all zero values
Initialized openingvalue with all zero values
Initialized closingvalue with all zero values
```

**Test API directly:**
```bash
# Test data endpoint
curl http://localhost:8080/api/data

# You should see JSON like:
# {"data":[{"variable":"tradeprofit","maximum":"0","minimum":"0",...}]}
```

### Problem 5: Port Already in Use
**Symptoms:** "Address already in use" error

**Solution:**
```bash
# Find and kill the process using port 8080
lsof -i :8080
kill -9 <PID>

# Or use a different port (modify WebServerApplication.java line with 8080)
```

## Step-by-Step Fresh Start

### Complete Reset (Recommended)

```bash
# 1. Stop the server (Ctrl+C)

# 2. Delete database
rm -f WebAppDataBase.db WebAppDataBase.sql

# 3. Clean build
mvn clean compile

# 4. Verify HTML file exists
ls -l trade-index.html

# 5. Start server
mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"

# 6. Open browser to http://localhost:8080

# 7. Check browser console (F12) for any JavaScript errors

# 8. Load example values
# Click "📋 Load Examples" button
```

## Debugging Checklist

### Server-Side
- [ ] Server started successfully
- [ ] No compilation errors
- [ ] Database file created (`WebAppDataBase.db`)
- [ ] Console shows "Table is empty - populating with zero values"
- [ ] Console shows all 5 variables initialized
- [ ] API responding to GET /api/data

### Client-Side
- [ ] Browser opened to correct URL (http://localhost:8080)
- [ ] HTML loaded (check browser title: "Trade Calculator - Trade_Function")
- [ ] No JavaScript errors in console (F12 → Console tab)
- [ ] Network tab shows successful API calls
- [ ] SELL/BUY buttons visible
- [ ] Table structure visible (headers showing)

### Expected Console Output

**Server console:**
```
Trade Web Server running at: http://localhost:8080
Table is empty - populating with zero values...
Initialized tradeprofit with all zero values
Initialized profitfactor with all zero values
Initialized tradeamount with all zero values
Initialized openingvalue with all zero values
Initialized closingvalue with all zero values
```

**Browser console (F12):**
```
Page loaded, initializing...
Loading data from API...
API Response: {data: Array(5)}
Data loaded successfully: 5 records
Populating table with 5 rows
Adding row for: tradeprofit
Adding row for: profitfactor
Adding row for: tradeamount
Adding row for: openingvalue
Adding row for: closingvalue
```

## Common Errors and Fixes

### Error: "Variable not found: buyvariable"
**Cause:** Old database with old variable names

**Fix:**
```bash
rm WebAppDataBase.db
# Restart server - it will create new database with correct names
```

### Error: "No data available"
**Cause:** Database empty or API not returning data

**Fix:**
1. Click "📋 Load Examples" button
2. Or manually enter values and click "▶️ Run Calculations"

### Error: "Connection refused"
**Cause:** Server not running

**Fix:**
```bash
# Start the server
mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"
```

### Error: "Cannot read properties of null"
**Cause:** DOM elements not found - HTML file issue

**Fix:**
1. Verify `trade-index.html` exists in project root
2. Check file has correct content (should have `<table id="dataTable">`)
3. Clear browser cache and reload

## Verification Steps

### 1. Verify Database Schema
```bash
# Install sqlite3 if needed
sudo apt-get install sqlite3

# Check database
sqlite3 WebAppDataBase.db

# Run these commands in sqlite3:
.schema
SELECT * FROM WebAppDataBase;
.quit
```

**Expected output:**
```sql
CREATE TABLE WebAppDataBase (
    variable VARCHAR(50) DEFAULT '0',
    maximum DECIMAL(20,8) DEFAULT 0,
    minimum DECIMAL(20,8) DEFAULT 0,
    returnmin DECIMAL(20,8) DEFAULT 0,
    returnmax DECIMAL(20,8) DEFAULT 0
);

-- And 5 rows with variables:
-- tradeprofit, profitfactor, tradeamount, openingvalue, closingvalue
```

### 2. Verify API Endpoints
```bash
# Test GET /api/data
curl http://localhost:8080/api/data | jq

# Test POST /api/update
curl -X POST http://localhost:8080/api/update \
  -H "Content-Type: application/json" \
  -d '{"variable":"tradeamount","column":"minimum","value":"10000"}'

# Test POST /api/query
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{
    "tradeAction":"SELL",
    "spread":0.01,
    "ratePK":17.7055,
    "ratePN":1.0,
    "basedOnMarketRate":false
  }' | jq
```

### 3. Verify Web Page Elements
Open browser DevTools (F12) and run in Console:
```javascript
// Check if elements exist
console.log('Table:', document.getElementById('dataTable'));
console.log('Loading:', document.getElementById('loading'));
console.log('Status:', document.getElementById('status'));
console.log('Spread input:', document.getElementById('spread'));

// Check current data
console.log('Current data:', currentData);

// Test API from browser
fetch('/api/data')
  .then(r => r.json())
  .then(d => console.log('API Data:', d));
```

## Still Having Issues?

### Enable Detailed Logging

**In Java (WebServerApplication.java):**
Already has detailed logging with System.out.println statements

**In Browser:**
Already has console.log statements for debugging

### Check File Locations
```bash
# Verify all files are in correct locations
tree -L 3 src/
ls -la *.html
ls -la WebAppDataBase.*
```

**Expected structure:**
```
src/
├── main/
│   └── java/
│       └── co/za/Main/
│           ├── TradeModules/
│           │   ├── Trade_Function.java
│           │   └── TradeAction.java
│           └── WebTradeApplication/
│               ├── WebApp.java
│               ├── WebServerApplication.java
│               ├── WebAppDataBase.java
│               └── WebQueryImplementation.java
└── test/
    └── java/
        └── co/za/MainTest/
            └── Test_Trade_Function.java

trade-index.html (in project root)
```

## Quick Test Script

Create `test_api.sh`:
```bash
#!/bin/bash
echo "Testing API endpoints..."

echo -e "\n1. GET /api/data"
curl -s http://localhost:8080/api/data | jq '.'

echo -e "\n2. Update value"
curl -s -X POST http://localhost:8080/api/update \
  -H "Content-Type: application/json" \
  -d '{"variable":"tradeamount","column":"minimum","value":"10000"}' | jq '.'

echo -e "\n3. Run query"
curl -s -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{"tradeAction":"SELL","spread":0.01,"ratePK":17.7055,"ratePN":1.0,"basedOnMarketRate":false}' | jq '.'

echo -e "\n4. GET /api/data again"
curl -s http://localhost:8080/api/data | jq '.'
```

Run: `chmod +x test_api.sh && ./test_api.sh`

## Contact Points

If issues persist:
1. Check console output for specific error messages
2. Verify Java version: `java -version` (should be 21+)
3. Check Maven version: `mvn -version`
4. Review logs in terminal where server is running
5. Check browser console for JavaScript errors

---

**Last Updated:** December 14, 2025