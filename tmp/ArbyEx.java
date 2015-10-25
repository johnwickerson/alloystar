package tmp;

import java.util.Set;

import kodkod.engine.hol.HOLTranslation;
import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Pair;
import edu.mit.csail.sdg.alloy4.Pos;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;

public class ArbyEx {

    private static String model = "module ArbyMod__1421443078_037_5717\n\natomsig Graph__0 : Graph\n\natomsig Node__0 : Node\n\natomsig Node__1 : Node\n\natomsig Edge__0 : Edge\n// =============================================\n// == module GraphModel\n\nsig Node  {\n  val: lone Int\n}\n\nsig Edge  {\n  src: one Node,\n  dst: one Node\n} {\n  this.@src != this.@dst\n}\n\nsig Graph  {\n  nodes: set Node,\n  edges: set Edge\n} {\n  this.@edges.(@src + @dst) in this.@nodes\n}\n\nfun valsum[nodes: set Node]: Int {\n  sum n: nodes {\n    n.val\n  }\n}\n\npred hampath[g: Graph, path: seq (Node)] {\n  path[Int] = g.nodes\n  #path = #g.nodes\n  all i: {i: Int | i >= 0 && i < minus[#path, 1]} {\n    some e: g.edges {\n      e.src = path[i]\n      e.dst = path[plus[i, 1]]\n    }\n  }\n}\n\npred kColor[g: Graph, k: Int, ans: Node -> one Int] {\n  ans.Int = g.nodes\n  ans[Node] in ({i: Int | i >= 0 && i < k})\n  all e: g.edges {\n    ans[e.src] != ans[e.dst]\n  }\n}\n\npred clique[g: Graph, clq: set Node] {\n  clq in g.nodes\n  all n1: clq {\n    all n2: clq - n1 {\n      some e: g.edges {\n        e.src = n1 && e.dst = n2 || e.src = n2 && e.dst = n1\n      }\n    }\n  }\n}\n\npred maxClique[g: Graph, clq: set Node] {\n  clique[g, clq]\n  no clq2: set Node {\n    clq2 != clq\n    clique[g, clq2]\n    #clq2 > #clq\n  }\n}\n\npred maxCliqueFix[g: Graph, clq: set Node] {\n  fix clique[g, clq] until #clq > #_clq\n}\n\npred maxMaxClique[g: Graph, clq: set Node] {\n  maxClique[g, clq]\n  no clq2: set Node {\n    clq2 != clq\n    maxClique[g, clq2]\n    valsum[clq2] > valsum[clq]\n  }\n}\n\npred maxMaxCliqueFix[g: Graph, clq: set Node] {\n  \n        fix maxClique[g, clq]\n        until {\n          valsum[clq] > valsum[_clq]\n        }\n        \n}\n\npred maxCliqueFixFix[g: Graph, clq: set Node] {\n  \n        fix maxCliqueFix[g, clq]\n        until {\n          valsum[clq] > valsum[_clq]\n        }\n        \n}\n\npred noClique[g: Graph, n: Int] {\n  no clq: set Node {\n    #clq = n\n    clique[g, clq]\n  }\n}\n\npred noSymEdges[g: Graph] {\n  no e1: g.edges, e2: g.edges {\n    e1 != e2\n    e1.src = e2.dst && e1.dst = e2.src || e1.src = e2.src && e1.dst = e2.dst\n  }\n}\n\nassert hampath_reach {\n  all g: Graph, path: seq (Node) {\n    hampath[g, path] => g.nodes in (path[0]).*(~src.dst)\n  }\n}\n\nassert hampath_uniq {\n  all g: Graph, path: seq (Node) {\n    hampath[g, path] => (all n: g.nodes {\n      #path.n = 1\n    }\n    )\n  }\n}\n\nassert kColor_props {\n  all g: Graph, k: Int, ans: Node -> one Int {\n    k > 0 && kColor[g, k, ans] => #ans = #g.nodes && (no e: g.edges {\n      ans[e.src] = ans[e.dst]\n    }\n    ) && #ans[Node] <= k\n  }\n}\n\nassert clique_props {\n  all g: Graph, clq: set Node {\n    clique[g, clq] && noSymEdges[g] => #({e: g.edges | e.(src + dst) in clq}) = div[mul[(#clq), (minus[#clq, 1])], 2]\n  }\n}\n\nassert maxClique_props {\n  all g: Graph, clq: set Node {\n    #g.nodes = 2 && some g.edges && maxClique[g, clq] => g.nodes = clq\n  }\n}\n\nrun hampath for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\ncheck hampath_reach for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\ncheck hampath_uniq for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\nrun kColor for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\ncheck kColor_props for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\nrun clique for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\ncheck clique_props for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\nrun noClique for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n\ncheck maxClique_props for 5 but 5 Int, exactly 1 Graph, 5 Node, 10 Edge\n// -------------------------------------------\n\nrun hampath  for 4 but 10 Int";
    private static String pi = "\nuniverse = <Graph$Graph__0, Node$Node__0, Node$Node__1, Edge$Edge__0>\nlowers[Graph] = {<Graph$Graph__0>}\nlowers[Node] = {<Node$Node__0><Node$Node__1>}\nlowers[Edge] = {<Edge$Edge__0>}\nlowers[Graph.nodes] = {<Graph$Graph__0, Node$Node__0><Graph$Graph__0, Node$Node__1>}\nlowers[Graph.edges] = {<Graph$Graph__0, Edge$Edge__0>}\nlowers[Node.val] = {}\nlowers[Edge.src] = {<Edge$Edge__0, Node$Node__0>}\nlowers[Edge.dst] = {<Edge$Edge__0, Node$Node__1>}\nuppers[Graph] = {<Graph$Graph__0>}\nuppers[Node] = {<Node$Node__0><Node$Node__1>}\nuppers[Edge] = {<Edge$Edge__0>}\nuppers[Graph.nodes] = {<Graph$Graph__0, Node$Node__0><Graph$Graph__0, Node$Node__1>}\nuppers[Graph.edges] = {<Graph$Graph__0, Edge$Edge__0>}\nuppers[Node.val] = {}\nuppers[Edge.src] = {<Edge$Edge__0, Node$Node__0>}\nuppers[Edge.dst] = {<Edge$Edge__0, Node$Node__1>}\n\nints = <0, 1, 2>";
    private static int cmd_index = 9;

    public static void main(String[] args) throws Exception {
        A4Reporter rep = new A4Reporter() {

            @Override
            public void holCandidateFound(HOLTranslation tr, A4Solution candidate) {
                System.out.println(candidate);
            }

        };
        {
            System.out.println(model);
            Module world = CompUtil.parseEverything_fromString(rep, model);
            A4Options opt = new A4Options();
            opt.solver = A4Options.SatSolver.MiniSatJNI;
            opt.partialInstance = pi;
            opt.higherOrderSolver = true;
            opt.convertHolInst2A4Sol = true;
            opt.holMaxIter = 50;
            opt.holFullIncrements = false;
            opt.renameAtoms = false;
            opt.noOverflow = true;
            Command cmd = world.getAllCommands().get(cmd_index);
            A4Solution sol = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), cmd, opt);
            System.out.println(sol.getBitwidth());
            System.out.println(sol.getBounds());
            // eval with existing A4Solution
//            Expr e = CompUtil.parseOneExpression_fromString(world, "univ");
            System.out.println(sol);
            if (!sol.satisfiable()) {
                Pair<Set<Pos>, Set<Pos>> x = sol.highLevelCore();
                System.out.println(x);
            }
        }
    }
}
