//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zhaow.restful.navigator;

import com.intellij.icons.AllIcons.Actions;
import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.ui.treeStructure.CachingSimpleNode;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.util.OpenSourceUtil;
import com.zhaow.restful.common.KtFunctionHelper;
import com.zhaow.restful.common.PsiMethodHelper;
import com.zhaow.restful.common.ToolkitIcons;
import com.zhaow.restful.common.ToolkitIcons.METHOD;
import com.zhaow.restful.method.HttpMethod;
import com.zhaow.restful.method.action.ModuleHelper;
import com.zhaow.restful.navigation.action.RestServiceItem;
import gnu.trove.THashMap;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

public class RestServiceStructure extends SimpleTreeStructure {
    public static final Logger LOG = Logger.getInstance(RestServiceStructure.class);
    private SimpleTreeBuilder myTreeBuilder;
    private SimpleTree myTree;
    private static Project myProject;
    private RestServiceStructure.RootNode myRoot = new RestServiceStructure.RootNode();
    private int serviceCount = 0;
    private final RestServiceProjectsManager myProjectsManager;
    RestServiceDetail myRestServiceDetail;
    private final Map<RestServiceProject, RestServiceStructure.ProjectNode> myProjectToNodeMapping = new THashMap();

    public RestServiceStructure(Project project, RestServiceProjectsManager projectsManager, SimpleTree tree) {
        myProject = project;
        this.myProjectsManager = projectsManager;
        this.myTree = tree;
        this.myRestServiceDetail = (RestServiceDetail)project.getComponent(RestServiceDetail.class);
        this.myRestServiceDetail.project = project;
        this.configureTree(tree);
        this.myTreeBuilder = new SimpleTreeBuilder(tree, (DefaultTreeModel)tree.getModel(), this, (Comparator)null);
        Disposer.register(myProject, this.myTreeBuilder);
        this.myTreeBuilder.initRoot();
        this.myTreeBuilder.expand(this.myRoot, (Runnable)null);
    }

    private void configureTree(SimpleTree tree) {
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
    }

    public RestServiceStructure.RootNode getRootElement() {
        return this.myRoot;
    }

    public void update() {
        List<RestServiceProject> projects = RestServiceProjectsManager.getInstance(myProject).getServiceProjects();
        this.updateProjects(projects);
    }

    public void updateProjects(List<RestServiceProject> projects) {
        this.serviceCount = 0;
        Iterator var2 = projects.iterator();

        while(var2.hasNext()) {
            RestServiceProject each = (RestServiceProject)var2.next();
            this.serviceCount += each.serviceItems.size();
            RestServiceStructure.ProjectNode node = this.findNodeFor(each);
            if (node == null) {
                node = new RestServiceStructure.ProjectNode(this.myRoot, each);
                this.myProjectToNodeMapping.put(each, node);
            }
        }

        this.myTreeBuilder.getUi().doUpdateFromRoot();
        this.myRoot.updateProjectNodes(projects);
    }

    private RestServiceStructure.ProjectNode findNodeFor(RestServiceProject project) {
        return (RestServiceStructure.ProjectNode)this.myProjectToNodeMapping.get(project);
    }

    public void updateFrom(SimpleNode node) {
        if (node != null) {
            this.myTreeBuilder.addSubtreeToUpdateByElement(node);
        }

    }

    private void updateUpTo(SimpleNode node) {
        for(SimpleNode each = node; each != null; each = each.getParent()) {
            SimpleNode parent = each.getParent();
            this.updateFrom(each);
        }

    }

    public static <T extends RestServiceStructure.BaseSimpleNode> List<T> getSelectedNodes(SimpleTree tree, Class<T> nodeClass) {
        List<T> filtered = new ArrayList();
        Iterator var3 = getSelectedNodes(tree).iterator();

        while(var3.hasNext()) {
            SimpleNode node = (SimpleNode)var3.next();
            if (nodeClass != null && !nodeClass.isInstance(node)) {
                filtered.clear();
                break;
            }

            filtered.add((T) node);
        }

        return filtered;
    }

    private static List<SimpleNode> getSelectedNodes(SimpleTree tree) {
        List<SimpleNode> nodes = new ArrayList();
        TreePath[] treePaths = tree.getSelectionPaths();
        if (treePaths != null) {
            TreePath[] var3 = treePaths;
            int var4 = treePaths.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                TreePath treePath = var3[var5];
                nodes.add(tree.getNodeFor(treePath));
            }
        }

        return nodes;
    }

    private void resetRestServiceDetail() {
        this.myRestServiceDetail.resetRequestTabbedPane();
        this.myRestServiceDetail.setMethodValue(HttpMethod.GET.name());
        this.myRestServiceDetail.setUrlValue("URL");
        this.myRestServiceDetail.initTab((String)null);
    }

    public class ServiceNode extends RestServiceStructure.BaseSimpleNode {
        RestServiceItem myServiceItem;

        public ServiceNode(SimpleNode parent, RestServiceItem serviceItem) {
            super(parent);
            this.myServiceItem = serviceItem;
            Icon icon = METHOD.get(serviceItem.getMethod());
            if (icon != null) {
                this.getTemplatePresentation().setIcon(icon);
                this.setIcon(icon);
            }

        }

        protected SimpleNode[] buildChildren() {
            return new SimpleNode[0];
        }

        public String getName() {
            String name = this.myServiceItem.getName();
            return name;
        }

        public void handleSelection(SimpleTree tree) {
            RestServiceStructure.ServiceNode selectedNode = (RestServiceStructure.ServiceNode)tree.getSelectedNode();
            this.showServiceDetail(selectedNode.myServiceItem);
        }

        private void showServiceDetail(RestServiceItem serviceItem) {
            RestServiceStructure.this.myRestServiceDetail.switchToUrlPanel();
            RestServiceStructure.this.myRestServiceDetail.resetRequestTabbedPane();
            String method = serviceItem.getMethod() != null ? String.valueOf(serviceItem.getMethod()) : HttpMethod.GET.name();
            RestServiceStructure.this.myRestServiceDetail.setMethodValue(method);
            RestServiceStructure.this.myRestServiceDetail.setUrlValue(serviceItem.getFullUrl());
            String requestHeaders = "";
            String requestParams = "";
            String requestBodyJson = "";
            PsiElement psiElement = serviceItem.getPsiElement();
            if (psiElement.getLanguage() == JavaLanguage.INSTANCE) {
                PsiMethodHelper psiMethodHelper = PsiMethodHelper.create(serviceItem.getPsiMethod()).withModule(serviceItem.getModule());
                requestHeaders = psiMethodHelper.buildHeaderString();
                requestParams = psiMethodHelper.buildParamString();
                requestBodyJson = psiMethodHelper.buildRequestBodyJson();
            }

            RestServiceStructure.this.myRestServiceDetail.addRequestHeaderTab(requestHeaders);
            RestServiceStructure.this.myRestServiceDetail.addRequestParamsTab(requestParams);
            if (StringUtils.isNotBlank(requestBodyJson)) {
                RestServiceStructure.this.myRestServiceDetail.addRequestBodyTabPanel(requestBodyJson);
            }

        }

        public void handleDoubleClickOrEnter(SimpleTree tree, InputEvent inputEvent) {
            SimpleNode simpleNode = tree.getSelectedNode();
            if (simpleNode instanceof RestServiceStructure.ServiceNode) {
                RestServiceStructure.ServiceNode selectedNode = (RestServiceStructure.ServiceNode)simpleNode;
                RestServiceItem myServiceItem = selectedNode.myServiceItem;
                PsiElement psiElement = myServiceItem.getPsiElement();
                if (!psiElement.isValid()) {
                    RestServiceStructure.LOG.info("psiMethod is invalid: ");
                    RestServiceStructure.LOG.info(psiElement.toString());
                    RestServicesNavigator.getInstance(myServiceItem.getModule().getProject()).scheduleStructureUpdate();
                }

                if (psiElement.getLanguage() == JavaLanguage.INSTANCE) {
                    PsiMethod psiMethod = myServiceItem.getPsiMethod();
                    OpenSourceUtil.navigate(new Navigatable[]{psiMethod});
                }
            }

        }

        @Nullable
        @NonNls
        protected String getMenuId() {
            return "Toolkit.NavigatorServiceMenu";
        }
    }

    public class ProjectNode extends RestServiceStructure.BaseSimpleNode {
        List<RestServiceStructure.ServiceNode> serviceNodes = new ArrayList();
        RestServiceProject myRestProject;

        public ProjectNode(SimpleNode parent, RestServiceProject project) {
            super(parent);
            this.myRestProject = project;
            this.getTemplatePresentation().setIcon(ToolkitIcons.MODULE);
            this.setIcon(ToolkitIcons.MODULE);
            this.updateServiceNodes(project.serviceItems);
        }

        private void updateServiceNodes(List<RestServiceItem> serviceItems) {
            this.serviceNodes.clear();
            Iterator var2 = serviceItems.iterator();

            while(var2.hasNext()) {
                RestServiceItem serviceItem = (RestServiceItem)var2.next();
                this.serviceNodes.add(RestServiceStructure.this.new ServiceNode(this, serviceItem));
            }

            SimpleNode parent = this.getParent();
            if (parent != null) {
                ((RestServiceStructure.BaseSimpleNode)parent).cleanUpCache();
            }

            RestServiceStructure.this.updateFrom(parent);
        }

        protected SimpleNode[] buildChildren() {
            return (SimpleNode[])this.serviceNodes.toArray(new SimpleNode[this.serviceNodes.size()]);
        }

        public String getName() {
            return this.myRestProject.getModuleName();
        }

        @Nullable
        @NonNls
        protected String getActionId() {
            return "Toolkit.RefreshServices";
        }

        public void handleSelection(SimpleTree tree) {
            RestServiceStructure.this.myRestServiceDetail.switchToHostPanel();
            this.showModuleHostAndPort();
        }

        private void showModuleHostAndPort() {
            RestServiceStructure.this.resetRestServiceDetail();
            RestServiceStructure.this.myRestServiceDetail.setModuleHostPath((new ModuleHelper(this.myRestProject.module)).getServiceHostPrefix());
            RestServiceStructure.this.myRestServiceDetail.currentModule = this.myRestProject.module;
        }

        public void handleDoubleClickOrEnter(SimpleTree tree, InputEvent inputEvent) {
        }
    }

    public class RootNode extends RestServiceStructure.BaseSimpleNode {
        List<RestServiceStructure.ProjectNode> projectNodes = new ArrayList();

        protected RootNode() {
            super((SimpleNode)null);
         //   this.getTemplatePresentation().setIcon(Actions.Module);
          //  this.setIcon(Actions.Module);
        }

        protected SimpleNode[] buildChildren() {
            return (SimpleNode[])this.projectNodes.toArray(new SimpleNode[this.projectNodes.size()]);
        }

        public String getName() {
            String s = "Found %d services ";
            return String.format(s, RestServiceStructure.this.serviceCount);
        }

        public void handleSelection(SimpleTree tree) {
            RestServiceStructure.this.myRestServiceDetail.switchToUrlPanel();
            RestServiceStructure.this.resetRestServiceDetail();
        }

        public void updateProjectNodes(List<RestServiceProject> projects) {
            this.projectNodes.clear();
            Iterator var2 = projects.iterator();

            while(var2.hasNext()) {
                RestServiceProject project = (RestServiceProject)var2.next();
                RestServiceStructure.ProjectNode projectNode = RestServiceStructure.this.new ProjectNode(this, project);
                this.projectNodes.add(projectNode);
            }

            RestServiceStructure.this.updateFrom(this.getParent());
            this.childrenChanged();
        }
    }

    public abstract class BaseSimpleNode extends CachingSimpleNode {
        protected BaseSimpleNode(SimpleNode aParent) {
            super(aParent);
        }

        @Nullable
        @NonNls
        String getActionId() {
            return null;
        }

        @Nullable
        @NonNls
        String getMenuId() {
            return null;
        }

        public void cleanUpCache() {
            super.cleanUpCache();
        }

        protected void childrenChanged() {
            for(RestServiceStructure.BaseSimpleNode each = this; each != null; each = (RestServiceStructure.BaseSimpleNode)each.getParent()) {
                each.cleanUpCache();
            }

            RestServiceStructure.this.updateUpTo(this);
        }
    }
}
