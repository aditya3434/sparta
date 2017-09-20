package sparta.checkers.permission;

import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.qual.Unqualified;
import org.checkerframework.framework.source.Result;

import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.TreeUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;

import sparta.checkers.permission.qual.MayRequiredPermissions;
import sparta.checkers.permission.qual.RequiredPermissions;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;

/**
 * Propagate required permissions up the call stack. Require that they are
 * declared in the manifest.
 *
 * TODO: should we propagate required permissions from (anonymous) inner classes
 * to the outside?
 */
public class RequiredPermissionsVisitor extends BaseTypeVisitor<RequiredPermissionsAnnotatedTypeFactory> {

    public RequiredPermissionsVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
        ExecutableElement methodElt = visitMethodRequiredPermissions(node);
        visitMethodMayRequirePermissions(node, methodElt);
        return super.visitMethodInvocation(node, p);
    }

    @Override
    protected RequiredPermissionsAnnotatedTypeFactory createTypeFactory() {
        return new RequiredPermissionsAnnotatedTypeFactory(checker);
    }

    private void visitMethodMayRequirePermissions(MethodInvocationTree node,
            ExecutableElement methodElt) {
        // Look for @MayRequiredPermissions on the enclosing method
        AnnotationMirror mayReqP = atypeFactory.getDeclAnnotation(methodElt,
                MayRequiredPermissions.class);
        if (mayReqP != null) {
            List<String> mayReqPerms = AnnotationUtils.getElementValueArray(mayReqP, "value",
                    String.class, false);
            if (!mayReqPerms.isEmpty()) {
                ExecutableElement callerElt = TreeUtils.elementFromDeclaration(TreeUtils
                        .enclosingMethod(getCurrentPath()));
                AnnotationMirror callerReq = atypeFactory.getDeclAnnotation(callerElt,
                        MayRequiredPermissions.class);
                List<String> callerPerms;
                List<String> missing = new LinkedList<String>();
                if (callerReq == null) {
                    missing.addAll(mayReqPerms);
                    callerPerms = new LinkedList<String>();
                } else {
                    callerPerms = AnnotationUtils.getElementValueArray(callerReq, "value",
                            String.class, false);
                    for (String perm : mayReqPerms) {
                        if (!callerPerms.contains(perm)) {
                            missing.add(perm);
                        }
                    }
                }
                if (!missing.isEmpty()) {
                    checker.report(
                            Result.failure("may.required.permissions", missing,
                                    AnnotationUtils.getElementValue(mayReqP, "notes", String.class,
                                            false)), node);
                }
            }
        }
    }

    private ExecutableElement visitMethodRequiredPermissions(MethodInvocationTree node) {
        // Look for @RequiredPermissions on the enclosing method
        ExecutableElement methodElt = TreeUtils.elementFromUse(node);
        AnnotationMirror reqP = atypeFactory
                .getDeclAnnotation(methodElt, RequiredPermissions.class);
        if (reqP != null) {
            List<String> reqPerms = AnnotationUtils.getElementValueArray(reqP, "value",
                    String.class, false);
            if (!reqPerms.isEmpty()) {
                ExecutableElement callerElt = TreeUtils.elementFromDeclaration(TreeUtils
                        .enclosingMethod(getCurrentPath()));
                AnnotationMirror callerReq = atypeFactory.getDeclAnnotation(callerElt,
                        RequiredPermissions.class);
                List<String> callerPerms;
                List<String> missing = new LinkedList<String>();
                if (callerReq == null) {
                    missing.addAll(reqPerms);
                    callerPerms = new LinkedList<String>();
                } else {
                    callerPerms = AnnotationUtils.getElementValueArray(callerReq, "value",
                            String.class, false);
                    for (String perm : reqPerms) {
                        if (!callerPerms.contains(perm)) {
                            missing.add(perm);
                        }
                    }
                }
                if (!missing.isEmpty()) {
                    checker.report(Result.failure("unsatisfied.permissions", missing),
                            node);
                }
            }
        }
        return methodElt;
    }

    @Override
    public Void visitMethod(MethodTree node, Void p) {
        // Ensure that all constants in @RequiredPermissions are in Manifest
        return super.visitMethod(node, p);
    }
}
class RequiredPermissionsAnnotatedTypeFactory extends BaseAnnotatedTypeFactory{
    public RequiredPermissionsAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker, false);
        this.postInit();
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return Collections.unmodifiableSet(new HashSet<Class<? extends Annotation>>(Arrays.asList(Unqualified.class)));
    }
}