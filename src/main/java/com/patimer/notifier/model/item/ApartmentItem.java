package com.patimer.notifier.model.item;

import com.patimer.notifier.model.ItemType;
import com.patimer.notifier.model.SellerType;
import org.apache.commons.lang.Validate;

public class ApartmentItem extends SearchedItem
{
    private String link;
    private Integer price;
    private Double numberOfRooms;
    private Integer floorNumber;
    private Integer area;
    private String address;
    private SellerType sellerType;
    private AssetType assetType;

    public ApartmentItem(
        String link,
        Integer price,
        Double numberOfRooms,
        Integer floorNumber,
        Integer area,
        String address,
        SellerType sellerType,
        AssetType assetType
    )
    {
        Validate.notEmpty(link);
        Validate.isTrue(price == null || price > 0);
        Validate.isTrue(numberOfRooms == null || numberOfRooms > 0);
        Validate.isTrue(floorNumber == null || floorNumber > 0);
        Validate.isTrue(area == null || area > 0);
        Validate.notEmpty(address);

        this.link = link;
        this.price = price;
        this.numberOfRooms = numberOfRooms;
        this.floorNumber = floorNumber;
        this.area = area;
        this.address = address;
        this.sellerType = sellerType;
        this.assetType = assetType;
    }

    @Override
    public ItemType getItemType()
    {
        return ItemType.Apartment;
    }

    @Override
    public String getUniqueIdentifier()
    {
        // the unique identifier for apartment should be the link
        return link;
    }

    public String getLink()
    {
        return link;
    }

    public Integer getPrice()
    {
        return price;
    }

    public Double getNumberOfRooms()
    {
        return numberOfRooms;
    }

    public Integer getFloorNumber()
    {
        return floorNumber;
    }

    public Integer getArea()
    {
        return area;
    }

    public String getAddress()
    {
        return address;
    }

    public SellerType getSellerType()
    {
        return sellerType;
    }

    public double getPriceForMeter()
    {
        if(area != null && area != 0 )
        {
            return price / area;
        }
        else
        {
            return 0;
        }
    }

    public AssetType getAssetType()
    {
        return assetType;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApartmentItem that = (ApartmentItem) o;

        if (!link.equals(that.link)) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (numberOfRooms != null ? !numberOfRooms.equals(that.numberOfRooms) : that.numberOfRooms != null)
            return false;
        if (floorNumber != null ? !floorNumber.equals(that.floorNumber) : that.floorNumber != null) return false;
        if (area != null ? !area.equals(that.area) : that.area != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (sellerType != that.sellerType) return false;
        return assetType == that.assetType;

    }

    @Override
    public int hashCode()
    {
        int result = link.hashCode();
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (numberOfRooms != null ? numberOfRooms.hashCode() : 0);
        result = 31 * result + (floorNumber != null ? floorNumber.hashCode() : 0);
        result = 31 * result + (area != null ? area.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (sellerType != null ? sellerType.hashCode() : 0);
        result = 31 * result + (assetType != null ? assetType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "ApartmentItem{" +
            "link='" + link + '\'' +
            ", price=" + price +
            ", numberOfRooms=" + numberOfRooms +
            ", floorNumber=" + floorNumber +
            ", area=" + area +
            ", address='" + address + '\'' +
            ", sellerType=" + sellerType +
            ", assetType=" + assetType +
            '}';
    }
}
