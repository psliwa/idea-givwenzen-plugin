package org.psliwa.idea.givwenzen

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiAnnotationMemberValue
import com.intellij.psi.PsiBinaryExpression
import com.intellij.psi.PsiMethod

class DomainStep(val step: String, val method: PsiMethod) {
    companion object {
        fun from(method: PsiMethod): DomainStep? {
            fun annotationValueToString(value: PsiAnnotationMemberValue): String {
                when(value) {
                    is PsiBinaryExpression ->
                        return annotationValueToString(value.lOperand) + if(value.rOperand != null) annotationValueToString(value.rOperand!!) else ""
                    else ->
                        return StringUtil.unescapeBackSlashes(StringUtil.stripQuotesAroundValue(value.text))
                }
            }

            return method.modifierList.annotations.toList()
                    .filter { it -> it.qualifiedName.equals("org.givwenzen.annotations.DomainStep") }
                    .map { it -> it.parameterList.attributes.firstOrNull()?.value }
                    .filterNotNull()
                    .map(::annotationValueToString)
                    .map { it -> DomainStep(it, method)}
                    .firstOrNull()
        }
    }
}