package org.jetbrains.dokka.base.transformers.documentables

import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.model.DModule
import org.jetbrains.dokka.model.DPackage
import org.jetbrains.dokka.model.sourceSet
import org.jetbrains.dokka.plugability.DokkaContext
import org.jetbrains.dokka.transformers.documentation.PreMergeDocumentableTransformer

class EmptyPackagesFilterTransformer(val context: DokkaContext) : PreMergeDocumentableTransformer {
    override fun invoke(modules: List<DModule>): List<DModule> = modules.map { original ->
        val passOptions = context.configuration.passesConfigurations.first {
            original.sourceSets.contains(context.sourceSet(it))
        }
        original.let {
            EmptyPackagesFilter(passOptions).processModule(it)
        }
    }

    private class EmptyPackagesFilter(
        val passOptions: DokkaConfiguration.PassConfiguration
    ) {
        fun DPackage.shouldBeSkipped() = passOptions.skipEmptyPackages &&
                functions.isEmpty() &&
                properties.isEmpty() &&
                classlikes.isEmpty()

        fun processModule(module: DModule) = module.copy(
            packages = module.packages.filter { !it.shouldBeSkipped() }
        )
    }
}