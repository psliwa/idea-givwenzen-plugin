package org.psliwa.idea.givwenzen

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class DomainStepReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        when(element) {
            is PsiFile -> return referencesFromFile(element)
            else -> return emptyArray()
        }
    }

    private fun referencesFromFile(element: PsiFile): Array<PsiReference> {
        val text = element.text
        fun loop(s: Int, result: List<PsiReference>): List<PsiReference> {
            val subText = text.drop(s)
            val pipePos = subText.indexOfFirst { it -> it == '|' }

            if (pipePos >= 0) {
                fun whitespacePrefixLength(t: String): Int {
                    return t.takeWhile(Char::isWhitespace).length
                }

                val start = s + whitespacePrefixLength(subText)
                val end = s + pipePos - whitespacePrefixLength(subText.take(pipePos).reversed())
                val refs = if (start < end) listOf(DomainStepReference(element, TextRange(start, end))) else emptyList()
                return loop(s + pipePos + 1, refs + result)
            } else {
                return result
            }
        }

        return loop(1, emptyList()).toTypedArray()
    }
}