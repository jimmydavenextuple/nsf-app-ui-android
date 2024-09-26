package com.nextuple.nsf.ui.nav

enum class Screen(
	val title: String = "",
	val route: String,
	val isBackEnabled: Boolean = false,
	val isSearchEnabled: Boolean = false
) {
	LOGIN(route = "login"),
	HOME(title = "Store Fulfillment", route = "home", isSearchEnabled = true),
	PICK(title = "Pick Orders", route = "pick", isSearchEnabled = true),
	PREP(
		title = "Prep",
		route = "prep",
		isSearchEnabled = false
	), // TODO: Find a way around this to make this only false when trying to pack by gear and true otherwise
	ORDERS(title = "Orders", route = "orders", isSearchEnabled = true),
	ORDERS_DETAILS(
		title = "Order Details",
		route = "${ORDERS.route}_order_details",
		isBackEnabled = true
	),
	ORDERS_PICKUP(title = "Pickup", route = "${ORDERS.route}_pickup", isBackEnabled = true),
	SEARCH_RESULTS(title = "Search Results", route = "search_order", isBackEnabled = true),
	SETTINGS(title = "Settings", route = "settings", isBackEnabled = true),
	DEMO(title = "Demo", route = "demo", isBackEnabled = true),
	BARCODE_SCANNER(title = "Barcode Scanner", route = "barcode_scanner", isBackEnabled = true);

	companion object {
		fun getByRoute(route: String) = entries.firstOrNull {
			it.route == route.split("?", "/").first()
		} ?: HOME
	}
}
