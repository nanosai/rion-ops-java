package com.nanosai.rionops.rion.pojo;

public class PojoCyclic {

    public PojoCyclic parent = null;
    public PojoCyclic child  = null;

    /*
      Obj 3
        Key Short parent 7
        Obj null 1
        Key short child 6
        Obj 3
          Key Short parent 7
          Obj null 1
          Key short child 6
          Ref 2
    */
}
