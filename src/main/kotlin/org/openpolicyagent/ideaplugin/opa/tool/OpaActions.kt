package org.openpolicyagent.ideaplugin.opa.tool


import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project

import org.openpolicyagent.ideaplugin.ide.actions.fileDirectChildOfRoot
import org.openpolicyagent.ideaplugin.ide.actions.getImportsAsString
import org.openpolicyagent.ideaplugin.ide.actions.getPackageAsString

import org.openpolicyagent.ideaplugin.ide.extensions.OPAActionToolWindow
import org.openpolicyagent.ideaplugin.lang.psi.isRegoFile
import org.openpolicyagent.ideaplugin.openapiext.virtualFile

/**
 * Utility class to format Rego file with opaFmt
 *
 * @see org.openpolicyagent.ideaplugin.ide.actions.CheckDocumentAction
 */
class OpaActions : OpaBaseTool() {

    /**
     * Opens window running opa check on the current file, or popup if current
     * file is not a rego file
     */
    fun checkDocument(project: Project, document: Document, editor: Editor) {
        val title = "Opa Check"
        val file = document.virtualFile
        if (file != null && file.isRegoFile && file.isValid) {
            val opaWindow = OPAActionToolWindow()
            val pathToFile = file.path.removePrefix(project.basePath.toString()).removeRange(0, 1)
            val args = mutableListOf("check", pathToFile)
            opaWindow.runProcessInConsole(project, args, "Opa Check")
        } else {
            Notification("ActionNotPerformed", title, "Current file invalid or not Rego file", NotificationType.ERROR).notify(project)
        }
    }

    /**
     * Opens window running opa test --verbose on project directory
     */
     fun testWorkspace(project: Project) {
            val opaWindow = OPAActionToolWindow()
            val args = mutableListOf("test", ".", "--verbose")
            opaWindow.runProcessInConsole(project, args, "Opa Test")
    }

    fun testWorkspaceCoverage(project: Project) {
        val opaWindow = OPAActionToolWindow()
        val args = mutableListOf("test", "--coverage", "--format=json", ".")
        opaWindow.runProcessInConsole(project, args, "Opa Test Coverage")
    }

    /**
     * Outputs the trace for the selected text in current editor. Looks for input.json in project root
     * directory
     */
    fun traceSelection(project: Project, document: Document, editor: Editor){
        val opaWindow = OPAActionToolWindow()
        val text = editor.selectionModel.selectedText ?: return
        val pkg = getPackageAsString(document, project)
        val imports = getImportsAsString(document, project)
        val args = mutableListOf("eval", text, "--package", pkg, "--format", "pretty")

        //supply input.json from project root if exists
        val input_file = "input.json"
        if (fileDirectChildOfRoot(project, input_file)){
            args.add("--input")
            args.add(input_file)
        }

        args.add("--data=file:${project.basePath}")
        for (import in imports){
            args.add("--import")
            args.add(import)
        }
        args.add("--explain")
        args.add("full")

        opaWindow.runProcessInConsole(project, args, "Trace Selection")

    }

}
