package org.psliwa.idea.givwenzen

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScopesCore
import com.intellij.psi.search.scope.ProjectFilesScope
import java.util.regex.PatternSyntaxException

class DomainStepInContentFileReference(element: PsiElement) : PsiPolyVariantReferenceBase<PsiElement>(element) {
    override fun getVariants(): Array<Any> {
        return emptyArray()
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val scope = GlobalSearchScopesCore.filterScope(element.project, ProjectFilesScope.INSTANCE)
        return try {
            val text = element.text.removeSurrounding("\"")
            val regex = Regex("^" + text.replace("\\\\", "\\") + "$")
            FilenameIndex.getFilesByName(element.project, "content.txt", scope).asList()
                    .filter { file ->
                        DomainStepReferenceProvider.domainStepTextRanges(file).map { it.substring(file.text) }.any {
                            regex.matches(it)
                        }
                    }
                    .map(::PsiElementResolveResult)
                    .toTypedArray()
        } catch (e: PatternSyntaxException) {
            emptyArray()
        }
    }
}