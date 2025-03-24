module suihub::suihub_coffee;

use std::string::String;
use sui::balance::{Self, Balance};
use sui::coin::{Self, Coin};
use sui::package;
use sui::sui::SUI;
use sui::table::{Self, Table};

//Errors
const EWrongPaymentAmount: u64 = 1;

public struct ESPRESSO {}
public struct LUNGO {}

public struct SUIHUB_COFFEE has drop {}

public struct AdminCap has key {
    id: UID,
}

public struct SuiCoffee<phantom T> has key, store {
    id: UID,
    price: u64,
}

public struct SuiCoffeeMachine has key {
    id: UID,
    location: String,
    coffee_price: u64,
    profits: Balance<SUI>,
    espressos: Table<ID, u8>,
    lungos: Table<ID, u8>,
}

fun init(otw: SUIHUB_COFFEE, ctx: &mut TxContext) {
    package::claim_and_keep(otw, ctx);

    let admin_cap = AdminCap {
        id: object::new(ctx),
    };

    transfer::transfer(admin_cap, ctx.sender())
}

public fun create_coffee_machine(
    _: &mut AdminCap,
    location: String,
    coffee_price: u64,
    ctx: &mut TxContext,
) {
    let machine = SuiCoffeeMachine {
        id: object::new(ctx),
        location,
        coffee_price,
        profits: balance::zero(),
        espressos: table::new(ctx),
        lungos: table::new(ctx),
    };

    transfer::share_object(machine);
}

public fun update_coffee_price(
    _: &mut AdminCap,
    machine: &mut SuiCoffeeMachine,
    new_price: u64
) {
    machine.coffee_price = new_price;
}

public fun create_coffee<T>(
    machine: &mut SuiCoffeeMachine,
    payment: Coin<SUI>,
    ctx: &mut TxContext,
): SuiCoffee<T> {
    assert!(machine.coffee_price == coin::value(&payment), EWrongPaymentAmount);

    machine.profits.join(payment.into_balance());

    SuiCoffee<T> {
        id: object::new(ctx),
        price: machine.coffee_price,
    }
}

#[lint_allow(self_transfer)]
public fun take_profits_and_keep(
    _: &mut AdminCap,
    machine: &mut SuiCoffeeMachine,
    ctx: &mut TxContext,
) {
    let current_profits = balance::withdraw_all(&mut machine.profits);
    transfer::public_transfer(
        coin::from_balance(current_profits, ctx),
        tx_context::sender(ctx),
    )
}
