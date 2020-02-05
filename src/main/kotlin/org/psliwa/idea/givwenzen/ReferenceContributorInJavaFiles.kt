package org.psliwa.idea.givwenzen

import com.intellij.patterns.PsiJavaPatterns
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

class ReferenceContributorInJavaFiles : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
                PsiJavaPatterns.psiElement(PsiLiteralExpression::class.java)
                        .withSuperParent(3, PsiJavaPatterns.psiAnnotation().qName("org.givwenzen.annotations.DomainStep")),
                DomainStepReferenceProvider()
        )
    }
}