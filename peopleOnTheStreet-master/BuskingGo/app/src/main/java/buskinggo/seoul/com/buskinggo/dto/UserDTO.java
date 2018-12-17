package buskinggo.seoul.com.buskinggo.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserDTO implements Serializable{
    private int userNo;
    private String nickName;
    private String mainPlace;
    private String likeGenre;
    private int checkBusker;

    public UserDTO(int userNo, String nickName, String mainPlace, String likeGenre, int checkBusker){
        this.userNo = userNo;
        this.nickName = nickName;
        this.mainPlace = mainPlace;
        this.likeGenre = likeGenre;
        this.checkBusker = checkBusker;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getMainPlace() {
        return mainPlace;
    }

    public void setMainPlace(String mainPlace) {
        this.mainPlace = mainPlace;
    }

    public String getLikeGenre() {
        return likeGenre;
    }

    public void setLikeGenre(String likeGenre) {
        this.likeGenre = likeGenre;
    }

    public int getCheckBusker() {
        return checkBusker;
    }

    public void setCheckBusker(int checkBusker) {
        this.checkBusker = checkBusker;
    }
}
