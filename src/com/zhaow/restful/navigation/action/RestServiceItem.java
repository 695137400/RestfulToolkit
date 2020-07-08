//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zhaow.restful.navigation.action;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.codeStyle.NameUtil.MatchingCaseSensitivity;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.zhaow.restful.common.ToolkitIcons.METHOD;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.method.action.ModuleHelper;
import javax.swing.Icon;
import org.jetbrains.annotations.Nullable;

public class RestServiceItem implements NavigationItem {
    private PsiMethod psiMethod;
    private PsiElement psiElement;
    private Module module;
    private String requestMethod;
    private HttpMethod method;
    private String url;
    private Navigatable navigationElement;

    public RestServiceItem(PsiElement psiElement, String requestMethod, String urlPath) {
        this.psiElement = psiElement;
        if (psiElement instanceof PsiMethod) {
            this.psiMethod = (PsiMethod)psiElement;
        }

        this.requestMethod = requestMethod;
        if (requestMethod != null) {
            this.method = HttpMethod.getByRequestMethod(requestMethod);
        }

        this.url = urlPath;
        if (psiElement instanceof Navigatable) {
            this.navigationElement = (Navigatable)psiElement;
        }

    }

    @Nullable
    public String getName() {
        return this.url;
    }

    @Nullable
    public ItemPresentation getPresentation() {
        return new RestServiceItem.RestServiceItemPresentation();
    }

    public void navigate(boolean requestFocus) {
        if (this.navigationElement != null) {
            this.navigationElement.navigate(requestFocus);
        }

    }

    public boolean canNavigate() {
        return this.navigationElement.canNavigate();
    }

    public boolean canNavigateToSource() {
        return true;
    }

    public boolean matches(String queryText) {
        if (queryText.equals("/")) {
            return true;
        } else {
            MinusculeMatcher matcher = NameUtil.buildMatcher("*" + queryText, MatchingCaseSensitivity.NONE);
            return matcher.matches(this.url);
        }
    }

    public Module getModule() {
        return this.module;
    }

    public PsiMethod getPsiMethod() {
        return this.psiMethod;
    }

    public void setPsiMethod(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFullUrl() {
        if (this.module == null) {
            return this.getUrl();
        } else {
            ModuleHelper moduleHelper = ModuleHelper.create(this.module);
            return moduleHelper.getServiceHostPrefix() + this.getUrl();
        }
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public PsiElement getPsiElement() {
        return this.psiElement;
    }

    private class RestServiceItemPresentation implements ItemPresentation {
        private RestServiceItemPresentation() {
        }

        @Nullable
        public String getPresentableText() {
            return RestServiceItem.this.url;
        }

        @Nullable
        public String getLocationString() {
            String fileName = RestServiceItem.this.psiElement.getContainingFile().getName();
            String location = null;
            String projectName = RestServiceItem.this.psiElement.getContainingFile().getProject().getName();
            String filePath = RestServiceItem.this.psiElement.getContainingFile().getVirtualFile().getPath();
            String basePath = RestServiceItem.this.psiElement.getContainingFile().getProject().getBasePath();
            String packagePath = ((PsiJavaFileImpl)RestServiceItem.this.psiElement.getContainingFile()).getPackageName().replaceAll("[.]", "/");
            filePath = filePath.replace(basePath, "").replace(packagePath, "-");
            filePath = filePath.replace("-/" + fileName, "");
            if (RestServiceItem.this.psiElement instanceof PsiMethod) {
                PsiMethod psiMethod = (PsiMethod)RestServiceItem.this.psiElement;
                location = "{" + filePath.concat("}：").concat(psiMethod.getContainingClass().getName().concat("#").concat(psiMethod.getName()));
            }

            return "(" + location + ")";
        }

        @Nullable
        public Icon getIcon(boolean unused) {
            return METHOD.get(RestServiceItem.this.method);
        }
    }
}
