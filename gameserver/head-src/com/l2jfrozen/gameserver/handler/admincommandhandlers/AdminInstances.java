package com.l2jfrozen.gameserver.handler.admincommandhandlers;

import Dev.event.mod.interfaces.Instance;
import Dev.event.mod.interfaces.InstanceManager;
import com.l2jfrozen.gameserver.handler.IAdminCommandHandler;
import com.l2jfrozen.gameserver.model.L2Object;
import com.l2jfrozen.gameserver.model.L2Summon;
import com.l2jfrozen.gameserver.model.actor.instance.L2PcInstance;
import com.l2jfrozen.gameserver.model.zone.L2ZoneManager;
import com.l2jfrozen.gameserver.model.zone.L2ZoneType;
import com.l2jfrozen.gameserver.network.SystemMessageId;

import java.util.StringTokenizer;

public class AdminInstances implements IAdminCommandHandler {


    private static final String[] ADMIN_COMMANDS = {
            "admin_setinstance",
            "admin_create_instance",
            "admin_destroyinstance",
            "admin_listinstances",
            "admin_register_instance"
    };

    private enum CommandEnum {
        admin_create_instance,
        admin_setinstance,
        admin_destroyinstance,
        admin_listinstances,
        admin_register_instance
    }

    @Override
    public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        if (activeChar == null)
            return false;

        StringTokenizer st = new StringTokenizer(command);
        String firstToken = st.nextToken();

        CommandEnum comm;
        try {
            comm = CommandEnum.valueOf(firstToken);
        } catch (IllegalArgumentException e) {
            return false;
        }

        switch (comm) {
            case admin_create_instance:
                handleCreateInstance(st, activeChar);
                break;
            case admin_setinstance:
                handleSetInstance(st, activeChar);
                break;
            case admin_destroyinstance:
                handleDestroyInstance(st, activeChar);
                break;
            case admin_listinstances:
                handleListInstances(activeChar);
                break;
            case admin_register_instance:
                handleRegister(st, activeChar);
                break;
            default:
                return false;
        }

        return true;
    }

    private void handleCreateInstance(StringTokenizer st, L2PcInstance activeChar) {
        if (st.countTokens() < 2) {
            activeChar.sendMessage("Format: //createinstance <id> <templatefile>");
        } else {
            try {
                int id = Integer.parseInt(st.nextToken());
                if (InstanceManager.getInstance().createInstanceFromTemplate(id, "instance.xml") && id < 300000) {
                    activeChar.sendMessage("Instance created");
                } else {
                    activeChar.sendMessage("Cannot create instance");
                }
            } catch (Exception e) {
                activeChar.sendMessage("Error loading: " + st.nextToken());
            }
        }
    }

    private void handleRegister(StringTokenizer st ,L2PcInstance activeChar) {
       /* try
        {
            int val = Integer.parseInt(st.nextToken());
            if (InstanceManager.getInstance().getInstance(val) == null)
            {
                activeChar.sendMessage("La instancia no existe");
                return ;
            }

            L2Object target = activeChar.getTarget();
            if (target == null || target instanceof L2Summon)
            {
                activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
                return ;
            }
            if (target.isPlayer())
            {
                L2PcInstance player = (L2PcInstance) target;
                player.sendMessage("Se te te enviara a la instancia " + val);
                InstanceManager.getInstance().getInstance(val).onRegister(player);

               *//* L2Summon pet = player.getPet();
                if (pet != null)
                {
                    pet.setInstanceId(val);
                    pet.teleToLocation(pet.getX(), pet.getY(), pet.getZ());
                    player.sendMessage("GM отправил Вашего питомца " + pet.getName() + " в инстанс:" + val);
                }*//*
            }
            activeChar.sendMessage("Игрок " + target.getName() + " отправлен в инстанс " + target.getInstanceId());
            return ;
        }
        catch (Exception e)
        {
            activeChar.sendMessage("Используйте: //setinstance id");
        }*/

        InstanceManager.getInstance().getInstance(10).onRegister((L2PcInstance) activeChar.getTarget());

    }

    private void handleSetInstance(StringTokenizer st, L2PcInstance activeChar) {
        try
        {
            int val = Integer.parseInt(st.nextToken());
            if (InstanceManager.getInstance().getInstance(val) == null)
            {
                activeChar.sendMessage("Инстанс " + val + " не существует");
                return ;
            }

            L2Object target = activeChar.getTarget();
            if (target == null || target instanceof L2Summon)
            {
                activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
                return ;
            }
            target.setInstanceId(val);
            if (target.isPlayer())
            {
                L2PcInstance player = (L2PcInstance) target;
                player.sendMessage("GM отправил Вас в инстанс:" + val);
                InstanceManager.getInstance().getInstance(val).addPlayer(player.getObjectId());
                player.teleToLocation(player.getX(), player.getY(), player.getZ());
                L2Summon pet = player.getPet();
                if (pet != null)
                {
                    pet.setInstanceId(val);
                    pet.teleToLocation(pet.getX(), pet.getY(), pet.getZ());
                    player.sendMessage("GM отправил Вашего питомца " + pet.getName() + " в инстанс:" + val);
                }
            }
            activeChar.sendMessage("Игрок " + target.getName() + " отправлен в инстанс " + target.getInstanceId());
            return ;
        }
        catch (Exception e)
        {
            activeChar.sendMessage("Используйте: //setinstance id");
        }
    }

    private void handleDestroyInstance(StringTokenizer st, L2PcInstance activeChar) {
        try
        {
            int val = Integer.parseInt(st.nextToken());
            InstanceManager.getInstance().destroyInstance(val);
            activeChar.sendMessage("Инстанс удален");
        }
        catch (Exception e)
        {
            activeChar.sendMessage("Используйте: //destroyinstance id");
        }
    }

    private void handleListInstances(L2PcInstance activeChar) {
        for (Instance temp : InstanceManager.getInstance().getInstances().values()) {
            activeChar.sendMessage("Id: " + temp.getId() + " Name: " + temp.getName());
        }
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }



    /**
     * this method is called at initialization to register all the item ids automatically
     *
     * @return all known itemIds
     */

}
