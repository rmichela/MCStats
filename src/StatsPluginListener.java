//Copyright (C) 2010  Ryan Michela
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.


public class StatsPluginListener extends PluginListener {

	private StatsController controller;
	
	public StatsPluginListener(StatsController psm)
	{
		this.controller = psm;
	}
	
	@Override
	public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
		
		controller.placeABlock(player, blockPlaced);
		return super.onBlockPlace(player, blockPlaced, blockClicked, itemInHand);
	}

	@Override
	public boolean onBlockBreak(Player player, Block block) {
		controller.destroyABlock(player, block);
		return super.onBlockBreak(player, block);
	}

	@Override
	public void onDisconnect(Player player) {
		
		controller.logOut(player);
	}

	@Override
	public boolean onItemDrop(Player player, Item item) {
		
		controller.dropAnItem(player, item);
		
		return super.onItemDrop(player, item);
	}

	@Override
	public void onLogin(Player player) {
		controller.logIn(player);
	}

	@Override
	public void onPlayerMove(Player player, Location from, Location to) {
		controller.travelAMeter(player);
	}

	@Override
	public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
		
		// Did the attacker kill?
		if(defender instanceof LivingEntity) {
			LivingEntity livingDefender = (LivingEntity) defender;
				
			if(livingDefender.getHealth() - amount <= 0) {	
				// We have a successful kill
				
				// If the defender was a player, note the death
				if(livingDefender.isPlayer()) {
					controller.die(livingDefender.getPlayer());
				}
				
				// If the attacker was a player, note the kill
				if(attacker != null && attacker.isPlayer()) {
					controller.kill(attacker.getPlayer(), livingDefender);
				}
			}
		}
		
		
		
		return super.onDamage(type, attacker, defender, amount);
	}


	
}