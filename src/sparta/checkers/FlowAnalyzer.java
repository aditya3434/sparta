package sparta.checkers;

import org.checkerframework.javacutil.Pair;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sparta.checkers.quals.FlowPermission;
import sparta.checkers.quals.ParameterizedFlowPermission;

import com.sun.source.util.TreePath;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.DiagnosticSource;

/**
 * Class to perform extra processing on flow information.
 * 
 * Currently prints out a flow-policy of forbidden flows and a more verbose
 * breakdown.
 * 
 * Flows are categorized as either type flows or assignment flows. Type flows
 * are the flows corresponding to a single type (which has a source and sink).
 * 
 * Assignment flows are generated from assignment statements (or method calls) by
 * taking the source of the value argument and the sinks of the variable
 * argument.
 * 
 * @author mcarthur
 */

public class FlowAnalyzer {

    private static final String IMPLIED_FLOWS_FORBIDDEN_FILE_DEFAULT = "forbiddenFlows.txt";
    private static final String IMPLIED_FLOWS_VERBOSE_FILE_DEFAULT = "foundFlows.txt";
    private static final String ALL_FLOWS_FILE_DEFAULT = "forbiddenFlowLocations.txt";
    private static final String INTENT_FLOWS_FILE_DEFAULT = "intentFlows.txt";

    // TODO: would be nice if you could pass a file name
    private String impliedFlowsForbiddenFile = IMPLIED_FLOWS_FORBIDDEN_FILE_DEFAULT;
    private String impliedFlowsVerboseFile = IMPLIED_FLOWS_VERBOSE_FILE_DEFAULT;
    private String allFlowsFile = ALL_FLOWS_FILE_DEFAULT;
    private String intentFlowsFile = INTENT_FLOWS_FILE_DEFAULT;

    private final Set<Flow> forbiddenTypeFlows;
    private final Set<Flow> assignmentFlows;
    private final Set<Flow> forbiddenAssignmentFlows;
    private final Set<Flow> typeFlows;
    private final Set<Pair<TreePath, Flow>> allFlows;

    private final FlowPolicy flowPolicy;

    public FlowAnalyzer(FlowPolicy flowPolicy) {
        this.flowPolicy = flowPolicy;
        assignmentFlows = new HashSet<Flow>();
        typeFlows = new HashSet<Flow>();
        forbiddenAssignmentFlows = new HashSet<Flow>();
        forbiddenTypeFlows = new HashSet<Flow>();
        allFlows = new HashSet<Pair<TreePath, Flow>>();
    }
    
    public void printIntentFlowsByComponent() {
    	PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(intentFlowsFile));
            HashMap<String, String> componentFlows = new HashMap<String,String>();
            for (Pair<TreePath, Flow> pair : allFlows) {
            	Flow f = pair.second;
            	if(!f.toString().contains("INTENT")) {
            		continue;
            	}
                TreePath tree = pair.first;
                Flow flow = pair.second;
                String componentName = tree.getCompilationUnit().getSourceFile().getName();
                String[] compName = componentName.split("/");
                componentName = compName[compName.length-1];
                if(componentFlows.containsKey(componentName)) {
                	if(!componentFlows.get(componentName).contains(flow.toString())) {
                		componentFlows.put(componentName, componentFlows.get(componentName) + "\n" + flow.toString());
                	} 
                } else {
                	componentFlows.put(componentName, flow.toString());
                }
            }
            for(String key : componentFlows.keySet()) {
            	writer.println("Component: " + key);
            	writer.println(componentFlows.get(key));
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void printAllFlows() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(allFlowsFile));
            for (Pair<TreePath, Flow> pair : allFlows) {
                TreePath tree = pair.first;
                Flow flow = pair.second;
                writer.print(flow.toString());
                writer.print(" ## (");
                writer.print(tree.getCompilationUnit().getSourceFile().getName());
                writer.print(":");
                int pos = ((JCTree) tree.getLeaf()).getStartPosition();
                DiagnosticSource source = new DiagnosticSource(tree.getCompilationUnit()
                        .getSourceFile(), null);
                writer.print(source.getLineNumber(pos));
                writer.print(") ");
                writer.println(((JCTree) tree.getLeaf()).pos());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void printImpliedFlowsVerbose() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(impliedFlowsVerboseFile));
            printFlows(writer, getFlowStrList(
                    groupFlowsOnSource(typeFlows)),
                    "# Type Flows Grouped");

            printFlows(writer, getFlowStrList(
                    groupFlowsOnSource(
                    getForbiddenFlowsPairwise(
                    groupFlowsOnSource(forbiddenTypeFlows)))),
                    "# Forbidden Type Flows Grouped");

            printFlows(writer, getFlowStrList(
                    groupFlowsOnSource(assignmentFlows)),
                    "# Assignment Flows Grouped");

            printFlows(writer, getFlowStrList(
                    groupFlowsOnSource(
                    getForbiddenFlowsPairwise(
                    groupFlowsOnSource(forbiddenAssignmentFlows)))),
                    "# Forbidden Assignment Flows Grouped");

            printFlows(writer, getFlowStrList(typeFlows), "# Type Flows (as written)");
            printFlows(writer, getFlowStrList(forbiddenTypeFlows), "# Forbidden Type Flows (as written)");
            printFlows(writer, getFlowStrList(assignmentFlows), "# Assignment Flows (as written)");
            printFlows(writer, getFlowStrList(forbiddenAssignmentFlows),
                    "# Forbidden Assignment Flows (as written)");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public void printImpliedFlowsForbidden() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(impliedFlowsForbiddenFile));
            Set<Flow> forbiddenTypeFlowsGrouped = getForbiddenFlowsPairwise(groupFlowsOnSource(forbiddenTypeFlows));

            Set<Flow> forbiddenAssignmentFlowsGrouped = getForbiddenFlowsPairwise(groupFlowsOnSource(forbiddenAssignmentFlows));

            forbiddenAssignmentFlowsGrouped.addAll(forbiddenTypeFlowsGrouped);
            // Need to regroup on source because after adding two sets together.
            printFlows(writer, getFlowStrList(groupFlowsOnSource(forbiddenAssignmentFlowsGrouped)),
                    "# Flows currently forbidden");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Assumes there is only one source per Flow. Finds all sinks for that
     * source that are forbidden.
     * 
     * @param flows
     *            to check
     * @return Forbidden flows
     */
    private Set<Flow> getForbiddenFlowsPairwise(Collection<Flow> flows) {
        Set<Flow> results = new HashSet<Flow>();
        for (Flow flow : flows) {
            Set<ParameterizedFlowPermission> forbiddenSinks = new HashSet<ParameterizedFlowPermission>();
            for (ParameterizedFlowPermission sink : flow.sinks) {
                if (!flowPolicy.areFlowsAllowed(Pair.<Set<ParameterizedFlowPermission>, Set<ParameterizedFlowPermission>> of(
                        flow.sources, new HashSet<ParameterizedFlowPermission>(Arrays.asList(sink))))) {

                    forbiddenSinks.add(sink);
                }
            }
            results.add(new Flow(flow.sources, forbiddenSinks));
        }
        return results;
    }
    
    private Set<Flow> groupFlowsOnSource(Set<Flow> flows) {
        Map<ParameterizedFlowPermission, Flow> grouped = new HashMap<ParameterizedFlowPermission, Flow>();
        for (Flow flow : flows) {
            for (ParameterizedFlowPermission source : flow.sources) {
                Flow sourceSinks = grouped.get(source);
                if (sourceSinks == null) {
                    grouped.put(source, new Flow(source, flow.sinks));
                } else {
                    sourceSinks.sinks.addAll(flow.sinks);
                }
            }
        }
        return new HashSet<Flow>(grouped.values());
    }

    private void printFlows(PrintWriter writer, List<String> flows, String header) {
        writer.println(header);
        for (String flowStr : flows) {
            writer.println(flowStr);
        }
        writer.println("");
    }

    private List<String> getFlowStrList(Collection<Flow> flows) {
        List<String> result = new ArrayList<String>();
        for (Flow flow : flows) {
            result.add(flow.toString());
        }
        Collections.sort(result);
        return result;
    }

    public Set<Flow> getForbiddenTypeFlows() {
        return forbiddenTypeFlows;
    }

    public Set<Flow> getAssignmentFlows() {
        return assignmentFlows;
    }

    public Set<Flow> getForbiddenAssignmentFlows() {
        return forbiddenAssignmentFlows;
    }

    public Set<Flow> getTypeFlows() {
        return typeFlows;
    }

    public void setImpliedFlowsForbiddenFile(String impliedFlowsForbiddenFile) {
        this.impliedFlowsForbiddenFile = impliedFlowsForbiddenFile;
    }

    public void setImpliedFlowsVerboseFile(String impliedFlowsVerboseFile) {
        this.impliedFlowsVerboseFile = impliedFlowsVerboseFile;
    }

    public Set<Pair<TreePath, Flow>> getAllFlows() {
        return allFlows;
    }

    public String getAllFlowsFile() {
        return allFlowsFile;
    }

    public void setAllFlowsFiles(String allFlowsFile) {
        this.allFlowsFile = allFlowsFile;
    }
}
