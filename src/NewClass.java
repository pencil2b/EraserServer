
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Neptune
 */
public class NewClass {
    
    static Random rand;
    static int MAX_SIZE=100;
    private static final float start[][] = {{0,0},{0,MAX_SIZE},{MAX_SIZE,0},{MAX_SIZE,MAX_SIZE}};
    private static float init[];
    
    
    public static void main(String[] args) {
        rand = new Random();
        init = new float[2];
        init = start[1];
        while(true){
            int x = rand.nextInt(4);
            System.out.println(x);
            if(x==0)
                break;
        }
    }
}
