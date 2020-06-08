/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.github.vgramer.opaplugin.ide.runconfig

import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.Project

/**
 * Create RunConfiguration from ConfigurationType
 *
 * @link https://www.jetbrains.org/intellij/sdk/docs/basics/run_configurations/run_configuration_management.html#configuration-factory
 */
class OpaConfigurationFactory(type: ConfigurationType) : ConfigurationFactory(type) {
    override fun createTemplateConfiguration(project: Project) = when (type) {
        is OpaEvalRunConfigurationType -> OpaEvalRunConfiguration(project, this, "Opa eval")
        else -> throw IllegalArgumentException("No Opa run configuration type, but ${type.id} was received instead.")
    }


    override fun getName(): String {
        return "Opa configuration factory"
    }

}