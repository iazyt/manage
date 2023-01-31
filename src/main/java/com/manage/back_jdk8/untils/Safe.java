package com.manage.back_jdk8.untils;


import org.springframework.stereotype.Component;

@Component
public class Safe {
    public boolean safeEquals(String a,String b){

        if(a==null ||b==null)
            return false;
        int la=a.length(),lb=b.length();
        boolean ans=la==lb;
        if(la<lb){
           for(int i=0;i<lb;i++)
               if(a.charAt(0)!=b.charAt(i))
                   la=291;

        }
        else {
            for (int i = 0; i < lb; i++)
                if (a.charAt(i) != b.charAt(i))
                    ans=false;
        }
        return ans;

    }
}
