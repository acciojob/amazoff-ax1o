package com.driver;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Repository
public class OrderRepository {

    //order id(string) is key
    HashMap<String,Order> dbOrder = new HashMap<>();
    //partner id is key
    HashMap<String,DeliveryPartner> dbPartner = new HashMap<>();

    //partnerId is key and list of orders is value
    HashMap<String, List<String>> dbPair = new HashMap<>();

    public void addOrder(Order order){
        dbOrder.put(order.getId(),order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        dbPartner.put(partnerId,deliveryPartner);
    }

    public void addOrderPartnerPair(String orderId,String partnerId){
        List<String> list = new LinkedList<>();

        //increment number of orders assigned to delivery partner
        DeliveryPartner dp = dbPartner.get(partnerId);
        int orders = dp.getNumberOfOrders();
        dp.setNumberOfOrders(orders+1);


        if(dbPair.containsKey(partnerId)){
            list = dbPair.get(partnerId);
            list.add(orderId);
            dbPair.put(partnerId,list);
        }else{
            list.add(orderId);
            dbPair.put(partnerId,list);
        }


    }

    public Order getOrderById(String orderId){
        return dbOrder.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return dbPartner.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
//        return dbPartner.get(partnerId).getNumberOfOrders();
          if(dbPartner.containsKey((partnerId))){
              return dbPartner.get(partnerId).getNumberOfOrders();
          }

          return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        return dbPair.get(partnerId);
    }

    public List<String> getAllOrders(){
        List<String> list = new LinkedList<>();
        for(String order : dbOrder.keySet()){
            list.add(order);
        }
        return list;
    }

    public int getCountOfUnassignedOrders(){
        int totalOrders = dbOrder.size();

        int totalAssignedOrders = 0;

        for(String dp : dbPair.keySet()){
            totalAssignedOrders += dbPair.get(dp).size();
        }

        return totalOrders-totalAssignedOrders;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        List<String> orders = dbPair.get(partnerId);

        //conert time to int
        int givenMins = (Integer.parseInt(time.substring(0,2)) * 60) + (Integer.parseInt(time.substring(3,5)));

        int count = 0;
        for(String order: orders){
            if(dbOrder.get(order).getDeliveryTime() > givenMins)
                count++;
        }

        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId){
        int max = Integer.MIN_VALUE;
        for(String orderId : dbPair.get(partnerId)){

            max = Math.max(dbOrder.get(orderId).getDeliveryTime(),max);

        }

        double hrs = (double)max/60;
        String time = "";

        int temp = (int)hrs;

        if(temp >= 10){
            time = String.valueOf(temp) + ":";
        }else{
            time = "0" + String.valueOf(temp) + ":";
        }

        double hrMins = hrs - (double)temp;
        int mins = (int)Math.round(hrMins*60);

        if(mins >= 10){
            time += String.valueOf(mins);
        }else{
            time += "0"+ String.valueOf(mins);
        }

        return time;

    }

    public void deletePartnerById(String partnerId){
        dbPair.remove(partnerId);
        dbPartner.remove(partnerId);
    }

    public void deleteOrderById(String orderId){

        if(dbOrder.containsKey(orderId))
            dbOrder.remove(orderId);
        else
            return;

        for(String dp : dbPair.keySet()){

            List<String> orders = dbPair.get(dp);

            for(int i = 0 ; i < orders.size();i++){
                if(orderId.compareTo(orders.get(i)) == 0){
                    orders.remove(i);
                    dbPair.put(dp,orders);
                    return;
                }
            }

        }



    }


}
