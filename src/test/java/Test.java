import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;


public class Test {
	public static void main(String[] args) {
		try {
			String appToken =  UUID.randomUUID().toString();
			System.out.println("App Token : "+appToken+" --- Length : "+appToken.length());
			/*1 : Date : Feb 06, 2017*/
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("Enter a number : ");
			int number = 0;
			try {
				number = Integer.parseInt(br.readLine());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Reverse of Number : "+reverseNumber(number));
			
			/*2 : Date : Feb 13, 2017*/
			System.out.print("Enter a String : ");
			String input = br.readLine();
			System.out.println("Bamboo Search of String : "+bambooSearch(input));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Reverse a number without using predefined methods in Java
	public static int reverseNumber(int number){
		int remainder =  0;
		int temp = 0;
		while(number > 0){
			temp = number % 10;
			remainder = remainder * 10 + temp;
			number = number / 10;
		}
		return remainder;
	}
	
	//bamboo search of a string
	public static String bambooSearch(String input){
		String output = "";
		char[] c = input.toCharArray();
		for(int i=0; i<c.length; i++){
			boolean flag = false;
			if( i == 0){
				if(c[i] == c[i+1]){
					flag = true;
				}
			}else if(i == (c.length-1)){
				if(c[i] == c[i-1]){
					flag = true;
				}
			}else{
				if((c[i] == c[i+1]) || (c[i] == c[i-1])){
					flag = true;
				}
			}
			
			if(!flag){
				output += c[i];
			}
		}
		return output;
	}
}
