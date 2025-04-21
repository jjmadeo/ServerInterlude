package Dev.event.mod.interfaces;

public interface IEventEngine extends Runnable {


    public void load(); //acciones cuando el evento se inicializa.

    public void init(); //acciones cuando el evento se inicia, pero todabia no se ejecuta.

    public void start();//acciones cuando el evento  comienza a ejecutarse.

    public void stop();//acciones cuando el evento se termina

    public void onEntre(); //acciones cuando un jugador entra al evento
    public void onExit(); //acciones cuando un jugador sale del evento

    public void onEventTranscure(); //acciones que se ejecutan mientras el evento transcurre

    public void npcSpawn();  //acciones que se ejecutan cuando se spawnea los npc.



}
