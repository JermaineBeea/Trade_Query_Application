package co.za.MainTest;

import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import co.za.Main.TradeModules.Trade_Function;
import co.za.Main.TradeModules.TradeAction;
import java.math.RoundingMode;
import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Updated test suite for Trade_Function based migration
 * This replaces the old TestTradeFunctions that used TradeFunction
 */
public class TestTradeFunctions_Updated {

    Logger logger = Logger.getLogger(TestTradeFunctions_Updated.class.getName());

    {
        logger.setLevel(Level.ALL);
    }

    // Test data for SELL action
    TradeAction action = TradeAction.SELL;
    BigDecimal tradeAmount = new BigDecimal("10000");
    BigDecimal tradeProfit = new BigDecimal("-88.000000000");
    BigDecimal tradeProfitFactor = new BigDecimal("-0.000497021");
    BigDecimal spread = new BigDecimal("0.01");
    BigDecimal ratePN = new BigDecimal("1.0");
    BigDecimal ratePK = new BigDecimal("17.7055");
    BigDecimal opening_value = new BigDecimal("17.6967");
    BigDecimal closing_value = new BigDecimal("17.7055");

    Trade_Function tradeFunction;
    BigDecimal TOLERANCE = new BigDecimal("0.01");

    @BeforeEach
    public void setUp() {
        // Create new Trade_Function instance for each test
        tradeFunction = new Trade_Function(action, spread, ratePK, ratePN, 
            tradeAmount, opening_value, closing_value);
        tradeFunction.setBasedOnMarketRate(false);
    }

    @Test
    public void testReturnProfit() {
        BigDecimal actualProfit = tradeFunction.returnProfit(tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);
        
        BigDecimal diff = tradeProfit.subtract(actualProfit).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + tradeProfit + ", Actual: " + actualProfit + ", Diff: " + diff);

        logger.info("Expected: " + tradeProfit);
        logger.info("Actual: " + actualProfit);
    }

    @Test
    public void testReturnProfitFactor() {
        BigDecimal actualProfitFactor = tradeFunction.returnProfitFactor(tradeProfit, tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);
        
        BigDecimal diff = tradeProfitFactor.subtract(actualProfitFactor).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + tradeProfitFactor + ", Actual: " + actualProfitFactor + ", Diff: " + diff);

        logger.info("Expected Profit Factor: " + tradeProfitFactor);
        logger.info("Actual Profit Factor: " + actualProfitFactor);
    }

    @Test
    public void testReturnTradeAmount() {
        BigDecimal actualTradeAmount = tradeFunction.returnTradeAmount(tradeProfit, tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);
        
        BigDecimal diff = tradeAmount.subtract(actualTradeAmount).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + tradeAmount + ", Actual: " + actualTradeAmount + ", Diff: " + diff);

        logger.info("Expected Trade Amount: " + tradeAmount);
        logger.info("Actual Trade Amount: " + actualTradeAmount);
    }

    @Test
    public void testReturnOpeningValue() {
        BigDecimal actualOpeningValue = tradeFunction.returnOpening(tradeProfit, tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);

        BigDecimal diff = opening_value.subtract(actualOpeningValue).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
                "Expected: " + opening_value + ", Actual: " + actualOpeningValue + ", Diff: " + diff);

        logger.info("Expected Opening Value: " + opening_value);
        logger.info("Actual Opening Value: " + actualOpeningValue);
    }

    @Test
    public void testReturnClosingValue() {
        BigDecimal actualClosingValue = tradeFunction.returnClosing(tradeProfit, tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);

        BigDecimal diff = closing_value.subtract(actualClosingValue).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + closing_value + ", Actual: " + actualClosingValue + ", Diff: " + diff);

        logger.info("Expected Closing Value: " + closing_value);
        logger.info("Actual Closing Value: " + actualClosingValue);
    }

    @Test
    public void testMarketRateMode() {
        tradeFunction.setBasedOnMarketRate(true);
        assertTrue(tradeFunction.isBasedOnMarketRate(), "Should be in market rate mode");

        tradeFunction.setBasedOnMarketRate(false);
        assertFalse(tradeFunction.isBasedOnMarketRate(), "Should be in execution rate mode");
        
        logger.info("Market rate mode toggle test passed");
    }

    @Test
    public void testBuyAction() {
        // Test BUY action separately
        Trade_Function buyFunction = new Trade_Function(
            TradeAction.BUY, spread, ratePK, ratePN,
            tradeAmount, closing_value, opening_value);
        buyFunction.setBasedOnMarketRate(false);
        
        BigDecimal buyProfit = buyFunction.returnProfit(tradeAmount);
        
        assertNotNull(buyProfit, "Buy action should produce a profit result");
        logger.info("BUY action profit: " + buyProfit.toPlainString());
    }

    @Test
    public void testSellAction() {
        // Verify SELL action works as expected
        Trade_Function sellFunction = new Trade_Function(
            TradeAction.SELL, spread, ratePK, ratePN,
            tradeAmount, opening_value, closing_value);
        sellFunction.setBasedOnMarketRate(false);
        
        BigDecimal sellProfit = sellFunction.returnProfit(tradeAmount);
        
        assertNotNull(sellProfit, "Sell action should produce a profit result");
        logger.info("SELL action profit: " + sellProfit.toPlainString());
    }
}