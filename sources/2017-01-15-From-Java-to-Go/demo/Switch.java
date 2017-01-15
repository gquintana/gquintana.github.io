public class Switch {
  public static void main(String ... args) {
    int i=2;
    // tag::main[]
    switch i {
    case 0:
      System.out.print("None");
      break;
    case 1:
      System.out.print("Single ")
    default:
      System.out.println("Thing")
    }
    // end::main[]
  }
}