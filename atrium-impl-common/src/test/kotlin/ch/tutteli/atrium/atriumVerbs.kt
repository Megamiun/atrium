package ch.tutteli.atrium

import ch.tutteli.atrium.AssertionVerb.ASSERT
import ch.tutteli.atrium.AssertionVerb.EXPECT_THROWN
import ch.tutteli.atrium.creating.IAssertionPlant
import ch.tutteli.atrium.reporting.ReporterBuilder
import ch.tutteli.atrium.reporting.translating.ISimpleTranslatable
import ch.tutteli.atrium.spec.IAssertionVerbFactory

internal fun <T : Any> assert(subject: T)
    = AtriumFactory.newCheckImmediately(ASSERT, subject, AtriumReporterSupplier.REPORTER)

internal inline fun <T : Any> assert(subject: T, createAssertions: IAssertionPlant<T>.() -> Unit)
    = AtriumFactory.newCheckLazilyAtTheEnd(ASSERT, subject, AtriumReporterSupplier.REPORTER, createAssertions)

internal fun <T : Any?> assert(subject: T)
    = AtriumFactory.newNullable(ASSERT, subject, AtriumReporterSupplier.REPORTER)

internal fun expect(act: () -> Unit)
    = AtriumFactory.newThrowableFluent(EXPECT_THROWN, act, AtriumReporterSupplier.REPORTER)


internal enum class AssertionVerb(override val value: String) : ISimpleTranslatable {
    ASSERT("assert"),
    EXPECT_THROWN("expect the thrown exception"),
}

internal object AtriumReporterSupplier {
    val REPORTER by lazy {
        ReporterBuilder
            .withDetailedObjectFormatter()
            .withSameLineTextAssertionFormatter()
            .buildOnlyFailureReporter()
    }
}

internal object VerbSpec : ch.tutteli.atrium.spec.verbs.VerbSpec(
    "assert" to { subject -> assert(subject) },
    "assert" to { subject, createAssertions -> assert(subject, createAssertions) },
    "assert" to { subject -> assert(subject) },
    "expect" to { act -> expect { act() } })

internal object AssertionVerbFactory : IAssertionVerbFactory {
    override fun <T : Any> checkImmediately(subject: T) = assert(subject)
    override fun <T : Any> checkLazily(subject: T, createAssertions: IAssertionPlant<T>.() -> Unit)
        = assert(subject, createAssertions)

    override fun <T> checkNullable(subject: T) = assert(subject)
    override fun checkException(act: () -> Unit) = expect(act)
}