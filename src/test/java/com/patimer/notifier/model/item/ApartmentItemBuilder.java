package com.patimer.notifier.model.item;

import com.patimer.notifier.model.SellerType;

import java.util.UUID;

public class ApartmentItemBuilder
{
    private String link = "http://www.madlan.co.il/bulletin/" + UUID.randomUUID();
    private Integer price = 2000000;
    private Double numberOfRooms = 4.0;
    private Integer floorNumber = 4;
    private Integer area = 100;
    private String address = "Ben-Gurion, Tel-Aviv";
    private SellerType sellerType = SellerType.Private;
    private AssetType assetType = AssetType.Apartment;

    public ApartmentItemBuilder(){}

    public ApartmentItemBuilder(ApartmentItem apartmentItem)
    {
        this.link = apartmentItem.getLink();
        this.price = apartmentItem.getPrice();
        this.numberOfRooms = apartmentItem.getNumberOfRooms();
        this.floorNumber = apartmentItem.getFloorNumber();
        this.area = apartmentItem.getArea();
        this.address = apartmentItem.getAddress();
        this.sellerType = apartmentItem.getSellerType();
        this.assetType = apartmentItem.getAssetType();
    }

    public ApartmentItemBuilder withLink(String link)
    {
        this.link = link;
        return this;
    }

    public ApartmentItemBuilder withPrice(Integer price)
    {
        this.price = price;
        return this;
    }

    public ApartmentItemBuilder withNumberOfRooms(Double numberOfRooms)
    {
        this.numberOfRooms = numberOfRooms;
        return this;
    }

    public ApartmentItemBuilder withFloorNumber(Integer floorNumber)
    {
        this.floorNumber = floorNumber;
        return this;
    }

    public ApartmentItemBuilder withArea(Integer area)
    {
        this.area = area;
        return this;
    }

    public ApartmentItemBuilder withAddress(String address)
    {
        this.address = address;
        return this;
    }
    public ApartmentItemBuilder withSellerType(SellerType sellerType)
    {
        this.sellerType = sellerType;
        return this;
    }
    public ApartmentItemBuilder withAssetType(AssetType assetType)
    {
        this.assetType = assetType;
        return this;
    }

    public ApartmentItem build()
    {
        return new ApartmentItem(link, price, numberOfRooms, floorNumber, area, address, sellerType, assetType);
    }
}
