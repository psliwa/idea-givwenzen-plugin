package org.psliwa.idea.givwenzen

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import java.util.regex.PatternSyntaxException

class DomainStepInJavaMethodReference(private val element: PsiFile, private val textRange: TextRange) : PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {
    override fun getVariants(): Array<Any> {
        return emptyArray()
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = element.project
        val domainSteps = DomainStepIndex(project).findDomainSteps()

        return domainSteps.flatMap { domainStep ->
            try {
                val pattern = Regex("^"+domainStep.step+"$")
                if(pattern.containsMatchIn(textRange.substring(element.text))) {
                    listOf(domainStep.method)
                } else {
                    emptyList()
                }
            } catch (e: PatternSyntaxException) {
                emptyList<PsiMethod>()
            }
        }.map(::PsiElementResolveResult).toTypedArray()
    }
}