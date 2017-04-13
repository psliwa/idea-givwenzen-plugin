package org.psliwa.idea.givwenzen

import com.intellij.patterns.PlatformPatterns.psiFile
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar

class ReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(psiFile().withName("content.txt"), DomainStepReferenceProvider())
    }
}