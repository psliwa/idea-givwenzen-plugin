package org.psliwa.idea.givwenzen

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class DomainStepReference(private val element: PsiFile, private val textRange: TextRange) : PsiPolyVariantReferenceBase<PsiElement>(element, textRange) {
    override fun getVariants(): Array<Any> {
        return emptyArray()
    }

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        val project = element.project
        val domainSteps = DomainStepIndex(project).findDomainSteps()

        return domainSteps.flatMap { domainStep ->
            try {
                val pattern = Pattern.compile("^"+domainStep.step+"$")
                val matcher = pattern.matcher(textRange.substring(element.text))
                if(matcher.find()) {
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