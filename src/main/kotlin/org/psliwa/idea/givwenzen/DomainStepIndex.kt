package org.psliwa.idea.givwenzen

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch

class DomainStepIndex(val project: Project) {

    fun findDomainSteps(): List<DomainStep> {
        val scope = GlobalSearchScope.allScope(project)

        val domainSteps = JavaPsiFacade.getInstance(project)
                .findClasses("org.givwenzen.annotations.DomainSteps", scope)
                .flatMap { it -> AnnotatedElementsSearch.searchPsiClasses(it, scope) }
                .flatMap { it -> it.allMethods.toList() }
                .map { it -> DomainStep.from(it) }
                .filterNotNull()

        return domainSteps
    }

}