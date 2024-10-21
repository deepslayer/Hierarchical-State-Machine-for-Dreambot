package Framework.Interface;

public interface ValidatableState extends State{
    boolean isValid();// Determines if the state should be executed (based on conditions)
}
