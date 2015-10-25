open ../synth2[spec]

--------------------------------------------------------------------------------
-- Variables and Values
--------------------------------------------------------------------------------
one sig X1, X2, X3, X4, X5, X6, K extends IntVar {}
one sig I0, I1, I2, I3, I4, I5, I6 extends IntLit {}
fact {
  IntLit<:val = I0->0 + I1->1 + I2->2 + I3->3 + I4->4 + I5->5 + I6->6
}

--------------------------------------------------------------------------------
-- Specification
--------------------------------------------------------------------------------
fact { IntVarVal = Int }

pred spec[root: Node, eval: Node -> Int] {
  (eval[X1] < eval[X2] and eval[X2] < eval[X3] and eval[X3] < eval[X4] and eval[X4] < eval[X5] and eval[X5] < eval[X6]) implies
    (eval[K] < eval[X1] implies eval[root] = 0)
  (eval[X1] < eval[X2] and eval[X2] < eval[X3] and eval[X3] < eval[X4] and eval[X4] < eval[X5] and eval[X5] < eval[X6]) implies
    (eval[K] > eval[X6] implies eval[root] = 6)

  (eval[X1] < eval[X2] and eval[X2] < eval[X3] and eval[X3] < eval[X4] and eval[X4] < eval[X5] and eval[X5] < eval[X6]) implies
    ((eval[K] > eval[X1] and eval[K] < eval[X2]) implies eval[root] = 1)
  (eval[X1] < eval[X2] and eval[X2] < eval[X3] and eval[X3] < eval[X4] and eval[X4] < eval[X5] and eval[X5] < eval[X6]) implies
    ((eval[K] > eval[X2] and eval[K] < eval[X3]) implies eval[root] = 2)
  (eval[X1] < eval[X2] and eval[X2] < eval[X3] and eval[X3] < eval[X4] and eval[X4] < eval[X5] and eval[X5] < eval[X6]) implies
    ((eval[K] > eval[X3] and eval[K] < eval[X4]) implies eval[root] = 3)
  (eval[X1] < eval[X2] and eval[X2] < eval[X3] and eval[X3] < eval[X4] and eval[X4] < eval[X5] and eval[X5] < eval[X6]) implies
    ((eval[K] > eval[X4] and eval[K] < eval[X5]) implies eval[root] = 4)
  (eval[X1] < eval[X2] and eval[X2] < eval[X3] and eval[X3] < eval[X4] and eval[X4] < eval[X5] and eval[X5] < eval[X6]) implies
    ((eval[K] > eval[X5] and eval[K] < eval[X6]) implies eval[root] = 5)
}

--------------------------------------------------------------------------------
-- Commands
--------------------------------------------------------------------------------
run synthIntNodeI for 26 but -1..7 Int, 0 BoolLit, 0 BoolVar, 
                                        0 BinaryIntOp, 0 UnaryIntOp, 
                                        0 Equals, 0 BoolComp
run synthIntNodeI for 0 but -1..7 Int, exactly 7 ITE, exactly 6 LT
