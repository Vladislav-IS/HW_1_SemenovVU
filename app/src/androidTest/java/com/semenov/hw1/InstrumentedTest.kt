package com.semenov.hw1

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.FailureHandler
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.base.DefaultFailureHandler
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.screen.Screen
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import java.util.Random
import java.util.Locale
import java.util.concurrent.TimeUnit
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.ln
import org.apache.commons.math3.stat.inference.TestUtils
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.stat.StatUtils
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.hamcrest.Matcher
import org.junit.FixMethodOrder
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@LargeTest
internal class InstrumentedTest {

    // add/remove seed
    private val random = Random()
    var lastNumber = 0.0
    var widgetFlag = false

    private val limit = 1_500
    private var mean = 0.0
    private var variance = 1.0

    private var generatedNums = ArrayList<Double>(0)

    private var activityScenario: ActivityScenario<MainActivity>? = null
    private var handler: DescriptionFailureHandler? = null

    private lateinit var appContext: Context
    private lateinit var mInstrumentation: Instrumentation

    @Before
    fun setUp() {
        mInstrumentation = InstrumentationRegistry.getInstrumentation()
        handler = DescriptionFailureHandler(mInstrumentation)
        Espresso.setFailureHandler(handler)

        val nonLocalizedContext = mInstrumentation.targetContext
        val configuration = nonLocalizedContext.resources.configuration
        configuration.setLocale(Locale.UK)
        appContext = nonLocalizedContext.createConfigurationContext(configuration)

        mean = random.nextDouble()
        variance = random.nextDouble()

        val intent = Intent(appContext, MainActivity::class.java)
        activityScenario = ActivityScenario.launch(intent)

        meanId = appContext.resources
            .getIdentifier("mean_val", "id", appContext.opPackageName)
        varianceId = appContext.resources
            .getIdentifier("variance_value", "id", appContext.opPackageName)
        getNumId = appContext.resources
            .getIdentifier("get_random_num", "id", appContext.opPackageName)
        resultNumId = appContext.resources
            .getIdentifier("random_number_result", "id", appContext.opPackageName)
    }

    private fun checkInterface(ids: IntArray, message: String = "?") {
        var id = 1
        for (e in ids) {
            id *= e
        }
        if (message != "?") {
            Assert.assertNotEquals(message, 0, id.toLong())
        } else {
            Assert.assertNotEquals(0, id.toLong())
        }
    }

    @Test(timeout = MAX_TIMEOUT)
    fun distributionTest() {
        checkInterface(
            intArrayOf(
                meanId, varianceId, resultNumId
            )
        )
        distributionCheckStep()
    }

    private fun distributionCheckStep() {
        class SearchScreen : Screen<SearchScreen>() {
            val meanView = KEditText { withId(meanId) }
            val varianceView = KEditText { withId(varianceId) }
            val getNum = KButton { withId(getNumId) }
            val resultNum = KTextView { withId(resultNumId) }
        }

        val screen = SearchScreen()
        screen {
            meanView.clearText()
            varianceView.clearText()
            meanView.typeText("$mean")
            varianceView.typeText("$variance")
            var i = 0
            while (i <= limit) {
                getNum.click()
                Thread.sleep(THREAD_DELAY)
                resultNum.assert {
                    DoubleComparison(mean, this@InstrumentedTest)
                }
                i++
            }
            // check saving state after rotation
            handler?.extraMessage = "Rotating device"
            rotateDevice(true)
            resultNum.hasText("$lastNumber")
            rotateDevice(false)
            resultNum.hasText("$lastNumber")

            ksTest(generatedNums)
        }
    }

    fun addGeneratedNumber(e: Double) {
        generatedNums.add(ln(e))
    }

    /**
     * использую тест Колмогорова-Смирнова для
     * проверки нормаьности распределения логарифмов
     * сгенерированных чисел
     */
    private fun ksTest(a: ArrayList<Double>) {
        val d = a.toDoubleArray()
        val normDist = NormalDistribution(mean, sqrt(variance))
        val pValue = TestUtils.kolmogorovSmirnovTest(normDist, d)
        Log.d("KolmogorovSmirnovTest, p-value is", "$pValue")
        assertEquals("Distribution is not normal", true, pValue >= 0.05)
    }

    @Throws(InterruptedException::class)
    private fun rotateDevice(landscapeMode: Boolean) {
        if (landscapeMode) {
            activityScenario!!.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        } else {
            activityScenario!!.onActivity { activity ->
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    companion object {
        private const val THREAD_DELAY: Long = 10
        private const val MAX_TIMEOUT: Long = 500_000

        private var meanId = 0
        private var varianceId = 0
        private var getNumId = 0
        private var resultNumId = 0

        @BeforeClass
        @JvmStatic
        fun enableAccessibilityChecks() {
            IdlingPolicies.setMasterPolicyTimeout(5, TimeUnit.SECONDS);
            IdlingPolicies.setIdlingResourceTimeout(5, TimeUnit.SECONDS);
        }
    }
}

internal class DoubleComparison(
    private val mean: Double,
    private val testInstance: InstrumentedTest
) :
    ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException
        assertTrue(view is TextView)
        val gotValue = (view as TextView).text.toString()

        if (testInstance.widgetFlag || view.accessibilityClassName == "android.widget.TextView") {
            testInstance.widgetFlag = true
        } else {
            assertEquals("View has an incorrect accessibilityClassName", "TextView", "EditText")
        }
        val num = gotValue.toDouble()
        testInstance.lastNumber = num
        testInstance.addGeneratedNumber(num)
    }
}

class DescriptionFailureHandler(instrumentation: Instrumentation) : FailureHandler {
    var extraMessage = ""
    private var delegate: DefaultFailureHandler =
        DefaultFailureHandler(instrumentation.targetContext)

    override fun handle(error: Throwable?, viewMatcher: Matcher<View>?) {
        if (error != null) {
            val newError = Throwable(
                "$extraMessage " + error.message, error.cause
            )
            delegate.handle(newError, viewMatcher)
        }
    }
}
