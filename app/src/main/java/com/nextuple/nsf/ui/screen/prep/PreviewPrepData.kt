package com.nextuple.nsf.ui.screen.prep

import com.nextuple.nsf.retrofit.dto.PackTaskItem
import com.nextuple.nsf.retrofit.dto.ProductAttribute

val packTaskItem = PackTaskItem(
	sku = "2345",
	primaryAttr = ProductAttribute(name = "Color", value = "Cyclamen"),
	secondaryAttr = ProductAttribute(name = "Size", value = "7.5"),
	tertiaryAttr = ProductAttribute(name = "Style", value = "12345"),
	qty = 1,
	productName = "Hoka Women’s Clifton 9 Running Shoes",
	productImageUrls = listOf(
		"https://picsum.photos/1705",
		"https://picsum.photos/1726",
		"https://picsum.photos/1701"
	)
)
