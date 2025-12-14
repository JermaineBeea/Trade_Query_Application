package co.za.MainTest;

import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import co.za.Main.TradeModules.Trade_Function;
import co.za.Main.TradeModules.TradeAction;
import java.math.RoundingMode;
import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Test_Trade_Function {

    Logger logger = Logger.getLogger(Test_Trade_Function.class.getName());

    {
        logger.setLevel(Level.ALL);
    }

    TradeAction action = TradeAction.SELL;
    BigDecimal tradeAmount = new BigDecimal("10000");
    BigDecimal tradeProfit = new BigDecimal("-88.000000000");
    BigDecimal tradeProfitFactor = new BigDecimal("-0.000497021");
    BigDecimal spread = new BigDecimal("0.01");
    BigDecimal rateKN = new BigDecimal("1.0");
    BigDecimal rateBK = new BigDecimal("17.7055");
    BigDecimal opening_value = new BigDecimal("17.6967");
    BigDecimal closing_value = new BigDecimal("17.7055");

    Trade_Function tradeFunction = new Trade_Function(action, spread,
        rateBK, rateKN, tradeAmount,
        opening_value, closing_value);

    

    BigDecimal TOLERANCE = new BigDecimal("0.01");

    @BeforeEach
    public void setUp() {
        // Set to execution-based mode (basedOnMarketRate = false) for consistent test results
        tradeFunction.setBasedOnMarketRate(false);
    }

    @Test
    public void testReturnProfit() {
        BigDecimal actualProfit = tradeFunction.returnProfit(tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);
        
        // Use compareTo-based fuzzy check
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
        
        // Use compareTo-based fuzzy check
        BigDecimal diff = tradeAmount.subtract(actualTradeAmount).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + tradeAmount + ", Actual: " + actualTradeAmount + ", Diff: " + diff);

        logger.info("Expected Trade Amount: " + tradeAmount);
        logger.info("Actual Trade Amount: " + actualTradeAmount);
    }

    @Test
    public void testReturnopening_value() {
        BigDecimal tradeProfit = new BigDecimal("-88.000000000");
        BigDecimal actualopening_value = tradeFunction.returnOpening(tradeProfit, tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);

        BigDecimal diff = opening_value.subtract(actualopening_value).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
                "Expected: " + opening_value + ", Actual: " + actualopening_value + ", Diff: " + diff);

        logger.info("Expected Sell Variable: " + opening_value);
        logger.info("Actual Sell Variable: " + actualopening_value);
    }

    @Test
    public void testReturnclosing_value() {
        BigDecimal actualclosing_value = tradeFunction.returnClosing(tradeProfit, tradeAmount)
                .setScale(10, RoundingMode.HALF_UP);

        BigDecimal diff = closing_value.subtract(actualclosing_value).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + closing_value + ", Actual: " + actualclosing_value + ", Diff: " + diff);

        logger.info("Expected Buy Variable: " + closing_value);
        logger.info("Actual Buy Variable: " + actualclosing_value);
    }

    @Test
    public void testMarketRateMode() {
        // Test that the setter works
        tradeFunction.setBasedOnMarketRate(true);
        assertTrue(tradeFunction.isBasedOnMarketRate(), "Should be in market rate mode");

        tradeFunction.setBasedOnMarketRate(false);
        assertFalse(tradeFunction.isBasedOnMarketRate(), "Should be in execution rate mode");
        
        logger.info("Market rate mode toggle test passed");
    }
}