package co.za.Main.WebTradeApplication;

import java.math.BigDecimal;
import java.sql.SQLException;
import co.za.Main.TradeModules.Trade_Function;
import co.za.Main.TradeModules.TradeAction;

public class WebQueryImplementation {
    
    private TradeAction tradeAction;
    private BigDecimal spread;
    private BigDecimal ratePK;
    private BigDecimal ratePN;
    private boolean basedOnMarketRate;
    
    public WebQueryImplementation(TradeAction tradeAction, boolean basedOnMarketRate, 
                                BigDecimal spread, BigDecimal ratePK, BigDecimal ratePN) {
        this.tradeAction = tradeAction;
        this.basedOnMarketRate = basedOnMarketRate;
        this.spread = spread;
        this.ratePK = ratePK;
        this.ratePN = ratePN;
        
        System.out.println("=== Enhanced Trade Query Implementation Initialized ===");
        System.out.println("Trade Action: " + tradeAction);
        System.out.println("Based on Market Rate: " + basedOnMarketRate);
        System.out.println("Spread: " + spread);
        System.out.println("Rate PK: " + ratePK);
        System.out.println("Rate PN: " + ratePN);
    }
    
    public void populateTable(WebAppDataBase db) throws SQLException {
        System.out.println("=== Running Enhanced Trade Calculations ===");
        System.out.println("Trade Action: " + tradeAction);
        System.out.println("Calculation Mode: " + (basedOnMarketRate ? "MARKET-BASED" : "EXECUTION-BASED"));
        
        try {
            // Refresh input values first
            db.refreshInputValues();
            
            // Get all min/max values from database
            BigDecimal tradeProfitMax = db.getValueFromColumn("tradeprofit", "maximum");
            BigDecimal tradeProfitMin = db.getValueFromColumn("tradeprofit", "minimum");
            BigDecimal profitFactorMin = db.getValueFromColumn("profitfactor", "minimum");
            BigDecimal profitFactorMax = db.getValueFromColumn("profitfactor", "maximum");
            BigDecimal tradeAmountMax = db.getValueFromColumn("tradeamount", "maximum");
            BigDecimal tradeAmountMin = db.getValueFromColumn("tradeamount", "minimum");
            BigDecimal openingMin = db.getValueFromColumn("openingvalue", "minimum");
            BigDecimal openingMax = db.getValueFromColumn("openingvalue", "maximum");
            BigDecimal closingMin = db.getValueFromColumn("closingvalue", "minimum");
            BigDecimal closingMax = db.getValueFromColumn("closingvalue", "maximum");

            System.out.println("Current Input Values Retrieved:");
            System.out.println("- Trade Profit: " + tradeProfitMin + " to " + tradeProfitMax);
            System.out.println("- Trade Amount: " + tradeAmountMin + " to " + tradeAmountMax);
            System.out.println("- Opening Value: " + openingMin + " to " + openingMax);
            System.out.println("- Closing Value: " + closingMin + " to " + closingMax);
            System.out.println("- Profit Factor: " + profitFactorMin + " to " + profitFactorMax);

            // Calculate and update tradeprofit
            calculateTradeProfitValues(db, tradeAmountMin, tradeAmountMax, 
                                     openingMin, openingMax, 
                                     closingMin, closingMax);

            // Calculate and update profitfactor
            calculateProfitFactorValues(db, tradeProfitMin, tradeProfitMax,
                                      tradeAmountMin, tradeAmountMax,
                                      openingMin, openingMax,
                                      closingMin, closingMax);

            // Calculate and update tradeamount
            calculateTradeAmountValues(db, tradeProfitMin, tradeProfitMax, 
                                     openingMin, openingMax, 
                                     closingMin, closingMax);

            // Calculate and update openingvalue
            calculateOpeningValues(db, tradeProfitMin, tradeProfitMax, 
                                 tradeAmountMin, tradeAmountMax, 
                                 closingMin, closingMax);

            // Calculate and update closingvalue
            calculateClosingValues(db, tradeProfitMin, tradeProfitMax, 
                                 tradeAmountMin, tradeAmountMax, 
                                 openingMin, openingMax);

            // Export results
            db.exportToSQL();
            System.out.println("=== Enhanced Trade calculations completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Error during enhanced calculations: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Enhanced calculation failed: " + e.getMessage(), e);
        }
    }
    
    private void calculateTradeProfitValues(WebAppDataBase db, 
                                          BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                          BigDecimal openingMin, BigDecimal openingMax,
                                          BigDecimal closingMin, BigDecimal closingMax) throws SQLException {
        try {
            if (openingMax.compareTo(BigDecimal.ZERO) > 0 && closingMin.compareTo(BigDecimal.ZERO) > 0 &&
                openingMin.compareTo(BigDecimal.ZERO) > 0 && closingMax.compareTo(BigDecimal.ZERO) > 0) {
                
                Trade_Function funcMin = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMin, openingMin, closingMax);
                funcMin.setBasedOnMarketRate(basedOnMarketRate);
                
                Trade_Function funcMax = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMax, openingMax, closingMin);
                funcMax.setBasedOnMarketRate(basedOnMarketRate);
                
                BigDecimal tradeProfitMinResult = funcMin.returnProfit(tradeAmountMin);
                BigDecimal tradeProfitMaxResult = funcMax.returnProfit(tradeAmountMax);
                
                db.updateQueryResult("tradeprofit", tradeProfitMinResult, tradeProfitMaxResult);
                System.out.println("✅ Updated tradeprofit calculations");
            } else {
                System.out.println("⚠️ Skipping tradeprofit calculation - invalid input values");
                db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating tradeprofit: " + e.getMessage());
            db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateProfitFactorValues(WebAppDataBase db,
                                           BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                           BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                           BigDecimal openingMin, BigDecimal openingMax,
                                           BigDecimal closingMin, BigDecimal closingMax) throws SQLException {
        try {
            if (tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0 &&
                openingMax.compareTo(BigDecimal.ZERO) > 0 && closingMin.compareTo(BigDecimal.ZERO) > 0 &&
                openingMin.compareTo(BigDecimal.ZERO) > 0 && closingMax.compareTo(BigDecimal.ZERO) > 0) {
                
                Trade_Function funcMin = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMax, openingMin, closingMax);
                funcMin.setBasedOnMarketRate(basedOnMarketRate);
                
                Trade_Function funcMax = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMin, openingMax, closingMin);
                funcMax.setBasedOnMarketRate(basedOnMarketRate);
                
                BigDecimal profitFactorMinResult = funcMin.returnProfitFactor(tradeProfitMin, tradeAmountMax);
                BigDecimal profitFactorMaxResult = funcMax.returnProfitFactor(tradeProfitMax, tradeAmountMin);
                
                db.updateQueryResult("profitfactor", profitFactorMinResult, profitFactorMaxResult);
                System.out.println("✅ Updated profitfactor calculations");
            } else {
                System.out.println("⚠️ Skipping profitfactor calculation - invalid input values");
                db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating profitfactor: " + e.getMessage());
            db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateTradeAmountValues(WebAppDataBase db,
                                          BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                          BigDecimal openingMin, BigDecimal openingMax,
                                          BigDecimal closingMin, BigDecimal closingMax) throws SQLException {
        try {
            if (openingMax.compareTo(BigDecimal.ZERO) > 0 && openingMin.compareTo(BigDecimal.ZERO) > 0 &&
                closingMin.compareTo(BigDecimal.ZERO) > 0 && closingMax.compareTo(BigDecimal.ZERO) > 0) {
                
                Trade_Function funcMin = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    BigDecimal.ONE, openingMin, closingMax);
                funcMin.setBasedOnMarketRate(basedOnMarketRate);
                
                Trade_Function funcMax = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    BigDecimal.ONE, openingMax, closingMin);
                funcMax.setBasedOnMarketRate(basedOnMarketRate);
                
                BigDecimal tradeAmountMinResult = funcMin.returnTradeAmount(tradeProfitMin, BigDecimal.ONE);
                BigDecimal tradeAmountMaxResult = funcMax.returnTradeAmount(tradeProfitMax, BigDecimal.ONE);
                
                db.updateQueryResult("tradeamount", tradeAmountMinResult, tradeAmountMaxResult);
                System.out.println("✅ Updated tradeamount calculations");
            } else {
                System.out.println("⚠️ Skipping tradeamount calculation - invalid input values");
                db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating tradeamount: " + e.getMessage());
            db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateOpeningValues(WebAppDataBase db,
                                       BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                       BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                       BigDecimal closingMin, BigDecimal closingMax) throws SQLException {
        try {
            if (tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0 &&
                closingMin.compareTo(BigDecimal.ZERO) > 0 && closingMax.compareTo(BigDecimal.ZERO) > 0) {
                
                Trade_Function funcMin = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMax, BigDecimal.ONE, closingMax);
                funcMin.setBasedOnMarketRate(basedOnMarketRate);
                
                Trade_Function funcMax = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMin, BigDecimal.ONE, closingMin);
                funcMax.setBasedOnMarketRate(basedOnMarketRate);
                
                BigDecimal openingMinResult = funcMin.returnOpening(tradeProfitMin, tradeAmountMax);
                BigDecimal openingMaxResult = funcMax.returnOpening(tradeProfitMax, tradeAmountMin);
                
                db.updateQueryResult("openingvalue", openingMinResult, openingMaxResult);
                System.out.println("✅ Updated openingvalue calculations");
            } else {
                System.out.println("⚠️ Skipping openingvalue calculation - invalid input values");
                db.updateQueryResult("openingvalue", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating openingvalue: " + e.getMessage());
            db.updateQueryResult("openingvalue", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateClosingValues(WebAppDataBase db,
                                      BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                      BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                      BigDecimal openingMin, BigDecimal openingMax) throws SQLException {
        try {
            if (openingMin.compareTo(BigDecimal.ZERO) > 0 && openingMax.compareTo(BigDecimal.ZERO) > 0 &&
                tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0) {
                
                Trade_Function funcMin = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMax, openingMin, BigDecimal.ONE);
                funcMin.setBasedOnMarketRate(basedOnMarketRate);
                
                Trade_Function funcMax = new Trade_Function(tradeAction, spread, ratePK, ratePN, 
                    tradeAmountMin, openingMax, BigDecimal.ONE);
                funcMax.setBasedOnMarketRate(basedOnMarketRate);
                
                BigDecimal closingMinResult = funcMin.returnClosing(tradeProfitMin, tradeAmountMax);
                BigDecimal closingMaxResult = funcMax.returnClosing(tradeProfitMax, tradeAmountMin);
                
                db.updateQueryResult("closingvalue", closingMinResult, closingMaxResult);
                System.out.println("✅ Updated closingvalue calculations");
            } else {
                System.out.println("⚠️ Skipping closingvalue calculation - invalid input values");
                db.updateQueryResult("closingvalue", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating closingvalue: " + e.getMessage());
            db.updateQueryResult("closingvalue", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}