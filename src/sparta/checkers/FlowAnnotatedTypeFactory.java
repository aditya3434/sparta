package sparta.checkers;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;

import sparta.checkers.quals.ConservativeFlow;
import sparta.checkers.quals.NoFlow;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;

import checkers.quals.DefaultLocation;
import checkers.types.AnnotatedTypeMirror;
import checkers.types.BasicAnnotatedTypeFactory;
import checkers.util.InternalUtils;
import checkers.util.QualifierDefaults.DefaultApplier;


public class FlowAnnotatedTypeFactory extends BasicAnnotatedTypeFactory<FlowChecker> {

    public FlowAnnotatedTypeFactory(FlowChecker checker, CompilationUnitTree root) {
        super(checker, root);

        // Use the bottom type as default for everything but local variables.
        defaults.addAbsoluteDefault(checker.NOFLOWSOURCES, DefaultLocation.OTHERWISE);
        // Use the top type for local variables and let flow refine the type.
        defaults.addAbsoluteDefault(checker.ANYFLOWSOURCES, DefaultLocation.LOCALS);

        // Default is always the top annotation for sinks.
        defaults.addAbsoluteDefault(checker.NOFLOWSINKS, DefaultLocation.ALL);
        // But let's send null down any sink.
        this.treeAnnotator.addTreeKind(Tree.Kind.NULL_LITERAL, checker.ANYFLOWSINKS);

        this.postInit();
    }

    @Override
    protected void annotateImplicit(Tree tree, AnnotatedTypeMirror type) {
        Element element = InternalUtils.symbol(tree);
        handleDefaulting(element, type);
        super.annotateImplicit(tree, type);
    }

    @Override
    protected void annotateImplicit(Element element, AnnotatedTypeMirror type) {
        handleDefaulting(element, type);
        super.annotateImplicit(element, type);
    }

    protected void handleDefaulting(Element element, AnnotatedTypeMirror type) {
        while (element != null) {
            if (this.getDeclAnnotation(element, NoFlow.class) != null) {
                // Use no flow sources for the return type.
                new DefaultApplier(element, DefaultLocation.RETURNS, type).scan(type, checker.NOFLOWSOURCES);
                // Nothing needs to be done for parameters.
                // new DefaultApplier(element, DefaultLocation.PARAMETERS, type).scan(type, checker.NOFLOWSOURCES);
                return;
            } else if (this.getDeclAnnotation(element, ConservativeFlow.class) != null) {
                // Use the top type for return types in the Android packages
                defaults.addElementDefault(element, checker.ANYFLOWSOURCES, DefaultLocation.RETURNS);
                // Nothing needs to be done for parameters.
                // new DefaultApplier(element, DefaultLocation.PARAMETERS, type).scan(type, checker.NOFLOWSOURCES);
            }
            element = element.getEnclosingElement();
        }
    }

}
