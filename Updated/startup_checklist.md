# Quick Startup Checklist

## Before You Start

```bash
[ ] Java 21+ installed: java -version
[ ] Maven installed: mvn -version
[ ] All updated files in place
[ ] trade-index.html in project root
```

## Clean Start Procedure

### Step 1: Clean Database (Recommended First Time)
```bash
rm -f WebAppDataBase.db WebAppDataBase.sql
```

### Step 2: Set Java Path
```bash
source setPath.sh
```

### Step 3: Build Project
```bash
mvn clean compile
```
✅ Should complete without errors

### Step 4: Run Tests (Optional)
```bash
mvn test
```
✅ All tests should pass

### Step 5: Start Server
```bash
mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"
```

### Step 6: Verify Server Started
Look for in console:
```
✅ Trade Web Server running at: http://localhost:8080
✅ Table is empty - populating with zero values...
✅ Initialized tradeprofit with all zero values
✅ Initialized profitfactor with all zero values
✅ Initialized tradeamount with all zero values
✅ Initialized openingvalue with all zero values
✅ Initialized closingvalue with all zero values
```

### Step 7: Open Browser
```
URL: http://localhost:8080
```

### Step 8: Verify Web Page
```
✅ Page title: "Trade Calculator - Trade_Function"
✅ SELL/BUY buttons visible
✅ Parameter inputs visible (Spread, Rate PK, Rate PN)
✅ Table with 5 rows visible
✅ All buttons present (Run, Load Examples, Reset)
✅ No JavaScript errors in console (F12)
```

### Step 9: Load Example Data
```
✅ Click "📋 Load Examples"
✅ Confirm dialog appears
✅ Data populates in table
✅ Success message appears
```

### Step 10: Run First Calculation
```
✅ Verify SELL is selected
✅ Click "▶️ Run Calculations"
✅ Return Min/Max columns populate
✅ Success message shows
```

### Step 11: Test BUY Action
```
✅ Click "📈 BUY" button
✅ Click "▶️ Run Calculations"
✅ Different results appear
✅ Success message shows
```

## Quick Test Commands

### Test API (in another terminal)
```bash
# 1. Test data endpoint
curl http://localhost:8080/api/data

# Should return JSON with 5 variables

# 2. Test query endpoint
curl -X POST http://localhost:8080/api/query \
  -H "Content-Type: application/json" \
  -d '{"tradeAction":"SELL","spread":0.01,"ratePK":17.7055,"ratePN":1.0,"basedOnMarketRate":false}'

# Should return success and data
```

## Common Issues Quick Fix

| Issue | Quick Fix |
|-------|-----------|
| Port in use | `lsof -i :8080` then `kill -9 <PID>` |
| No data showing | `rm WebAppDataBase.db` and restart |
| Old variable error | `rm WebAppDataBase.db` and restart |
| Page not loading | Check `trade-index.html` exists in root |
| Compile errors | `mvn clean compile` |

## Expected Console Output

### Server Console:
```
[INFO] ------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------
Trade Web Server running at: http://localhost:8080
Table is empty - populating with zero values...
Initialized tradeprofit with all zero values
Initialized profitfactor with all zero values
Initialized tradeamount with all zero values
Initialized openingvalue with all zero values
Initialized closingvalue with all zero values
Press Ctrl+C to stop the server.
```

### Browser Console (F12):
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

## Files to Replace

Make sure these files are updated:
```
✅ src/main/java/co/za/Main/WebTradeApplication/WebQueryImplementation.java
✅ src/main/java/co/za/Main/WebTradeApplication/WebAppDataBase.java
✅ src/main/java/co/za/Main/WebTradeApplication/WebServerApplication.java
✅ trade-index.html (in project root)
✅ src/test/java/co/za/MainTest/TestTradeFunctions_Updated.java (optional)
```

## Database Variables Check

After first start, verify database has correct variables:
```bash
sqlite3 WebAppDataBase.db "SELECT variable FROM WebAppDataBase;"
```

Should show:
```
tradeprofit
profitfactor
tradeamount
openingvalue
closingvalue
```

NOT:
```
buyvariable   ❌ OLD NAME
sellvariable  ❌ OLD NAME
```

## Auto-Migration Check

If you had an old database, check for migration messages:
```
=== MIGRATION DETECTED ===
Migrating from old variable names to new variable names...
✅ Migrated sellvariable → openingvalue
✅ Migrated buyvariable → closingvalue
=== MIGRATION COMPLETE ===
```

## Stop Server

When done:
```bash
Ctrl+C in server terminal
```

## Restart Quickly

```bash
# Without database reset
mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"

# With database reset
rm WebAppDataBase.db && mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"
```

## One-Line Startup

```bash
source setPath.sh && mvn clean compile && mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"
```

## Automated Startup

```bash
chmod +x reset_and_start.sh
./reset_and_start.sh
```

---

## ✅ Success Indicators

You're good to go when you see ALL of these:

- [x] Server console shows no errors
- [x] "Trade Web Server running" message displayed
- [x] All 5 variables initialized in console
- [x] Browser opens to http://localhost:8080
- [x] SELL/BUY buttons visible and clickable
- [x] Table shows 5 rows with correct names
- [x] Load Examples populates data
- [x] Run Calculations produces results
- [x] Browser console (F12) has no errors
- [x] Status messages appear when clicking buttons

## 🎉 Ready!

Your Trade Application is now running with Trade_Function!

**Happy Trading! 🚀**

---

Print this checklist and keep it handy for quick reference!