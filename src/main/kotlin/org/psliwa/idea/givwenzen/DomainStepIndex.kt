package org.psliwa.idea.givwenzen

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiAnnotationMemberValue
import com.intellij.psi.PsiBinaryExpression
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch

class DomainStepIndex(val project: Project) {

    fun findDomainSteps(): List<DomainStep> {
        val scope = GlobalSearchScope.allScope(project)

        fun annotationValueToString(value: PsiAnnotationMemberValue): String {
            when(value) {
                is PsiBinaryExpression ->
                    return annotationValueToString(value.lOperand) + if(value.rOperand != null) annotationValueToString(value.rOperand!!) else ""
                else ->
                    return StringUtil.unescapeBackSlashes(StringUtil.stripQuotesAroundValue(value.text))
            }
        }

        val domainSteps = JavaPsiFacade.getInstance(project)
                .findClasses("org.givwenzen.annotations.DomainSteps", scope)
                .flatMap { it -> AnnotatedElementsSearch.searchPsiClasses(it, scope) }
                .flatMap { it -> it.allMethods.toList() }
                .flatMap { method ->
                    method.modifierList.annotations.toList()
                        .filter { it -> it.qualifiedName.equals("org.givwenzen.annotations.DomainStep") }
                        .map { it -> it.parameterList.attributes.firstOrNull()?.value }
                        .filterNotNull()
                        .map(::annotationValueToString)
                        .map { it -> DomainStep(it, method) }
                }

        return domainSteps
    }

}