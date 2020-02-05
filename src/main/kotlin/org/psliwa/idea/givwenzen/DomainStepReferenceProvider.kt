package org.psliwa.idea.givwenzen

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.util.ProcessingContext

class DomainStepReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        return when(element) {
            is PsiFile -> referencesFromFile(element)
            is PsiLiteralExpression -> referencesFromMethod(element)
            else -> emptyArray()
        }
    }

    private fun referencesFromMethod(element: PsiElement): Array<PsiReference> {
        return arrayOf(DomainStepInContentFileReference(element))
    }

    private fun referencesFromFile(element: PsiFile): Array<PsiReference> {
        return domainStepTextRanges(element).map { DomainStepInJavaMethodReference(element, it) }.toTypedArray()
    }

    companion object {
        fun domainStepTextRanges(element: PsiFile): List<TextRange> {
            val text = element.text
            fun loop(s: Int, result: List<TextRange>): List<TextRange> {
                val subText = text.drop(s)
                val pipePos = subText.indexOfFirst { it -> it == '|' }

                if (pipePos >= 0) {
                    fun whitespacePrefixLength(t: String): Int {
                        return t.takeWhile(Char::isWhitespace).length
                    }

                    val start = s + whitespacePrefixLength(subText)
                    val end = s + pipePos - whitespacePrefixLength(subText.take(pipePos).reversed())
                    val refs = if (start < end) listOf(TextRange(start, end)) else emptyList()
                    return loop(s + pipePos + 1, refs + result)
                } else {
                    return result
                }
            }

            return loop(1, emptyList())
        }
    }

}