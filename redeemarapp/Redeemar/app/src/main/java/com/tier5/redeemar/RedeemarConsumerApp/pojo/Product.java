package com.tier5.redeemar.RedeemarConsumerApp.pojo;

import java.util.ArrayList;

/**
 * 
 * first level item
 * 
 */
public class Product {

	private String pId, pName;
    private boolean opened;

	private ArrayList<SubCategory> mSubCategoryList;

	public Product(String pId, String pName, ArrayList<SubCategory> mSubCategoryList, boolean opened) {
		super();
        this.pId = pId;
		this.pName = pName;
        this.opened = opened;
		this.mSubCategoryList = mSubCategoryList;
	}

	public String getpName() {
		return pName;
	}

	public boolean getOpened() {
		return opened;
	}

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public void setpName(String pName) {
		this.pName = pName;
	}

	public ArrayList<SubCategory> getmSubCategoryList() {
		return mSubCategoryList;
	}

	public void setmSubCategoryList(ArrayList<SubCategory> mSubCategoryList) {
		this.mSubCategoryList = mSubCategoryList;
	}


	public static class SubCategory {

		private String pId, pSubCatName;
        private boolean opened;
		private ArrayList<ItemList> mItemListArray;


		public SubCategory(String pId, String pSubCatName, ArrayList<ItemList> mItemListArray, boolean opened) {
			super();
            this.pId = pId;
			this.pSubCatName = pSubCatName;
            this.opened = opened;
			this.mItemListArray = mItemListArray;
		}

        public boolean getOpened() {
            return opened;
        }

        public void setOpened(boolean opened) {
            this.opened = opened;
        }

        public String getpId() {
            return pId;
        }

        public void setpId(String pId) {
            this.pId = pId;
        }

        public String getpSubCatName() {
			return pSubCatName;
		}

		public void setpSubCatName(String pSubCatName) {
			this.pSubCatName = pSubCatName;
		}

		public ArrayList<ItemList> getmItemListArray() {
			return mItemListArray;
		}

		public void setmItemListArray(ArrayList<ItemList> mItemListArray) {
			this.mItemListArray = mItemListArray;
		}

		/**
		 * 
		 * third level item
		 * 
		 */
		public static class ItemList {

			private String id, itemName;


			public ItemList(String id, String itemName) {
				super();
                this.id = id;
				this.itemName = itemName;

			}

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getItemName() {
				return itemName;
			}

			public void setItemName(String itemName) {
				this.itemName = itemName;
			}


		}

	}

}
