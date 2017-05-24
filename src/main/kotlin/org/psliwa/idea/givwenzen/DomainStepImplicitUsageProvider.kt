package org.psliwa.idea.givwenzen

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import java.util.regex.PatternSyntaxException

class DomainStepImplicitUsageProvider : ImplicitUsageProvider {
    override fun isImplicitWrite(element: PsiElement?): Boolean {
        return false
    }

    override fun isImplicitRead(element: PsiElement?): Boolean {
        return false
    }

    override fun isImplicitUsage(element: PsiElement?): Boolean {
        when(element) {
            is PsiMethod -> return isUsed(element)
            is PsiClass -> return isUsed(element)
            else -> return false
        }
    }

    private fun isUsed(psiClass: PsiClass): Boolean {
        return psiClass.modifierList?.annotations?.find { annotation -> annotation.qualifiedName.equals("org.givwenzen.annotations.DomainSteps") } != null &&
                psiClass.allMethods.find { method -> isUsed(method) } != null
    }

    private fun isUsed(method: PsiMethod): Boolean {
        return DomainStep.from(method).let {
            try {
                val contentFiles = FilenameIndex.getFilesByName(method.project, "content.txt", GlobalSearchScope.allScope(method.project))
                val pattern = "\\|\\s*" + it?.step + "\\s*\\|"
                contentFiles.find { file -> Regex(pattern).containsMatchIn(file.text) } != null
            } catch (e: PatternSyntaxException) {
                false
            }
        }
    }
}