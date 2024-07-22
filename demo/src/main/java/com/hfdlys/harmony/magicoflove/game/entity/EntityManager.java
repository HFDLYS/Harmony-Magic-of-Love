package com.hfdlys.harmony.magicoflove.game.entity;

import java.util.*;

import com.hfdlys.harmony.magicoflove.game.common.Hitbox;
import com.hfdlys.harmony.magicoflove.game.factory.CharacterFactory;
import com.hfdlys.harmony.magicoflove.game.factory.ObstacleFactory;
import com.hfdlys.harmony.magicoflove.game.factory.ProjectileFactory;
import com.hfdlys.harmony.magicoflove.game.factory.WeaponFactory;
import com.hfdlys.harmony.magicoflove.network.message.EntityManagerMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.CharacterRegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.EntityRegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ObstacleRegisterMessage;
import com.hfdlys.harmony.magicoflove.network.message.EntityRegister.ProjectileRegisterMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityManager {
    /**
     * 单体模式
     */
    private static EntityManager Instance = null;

    /**
     * 单体模式，获取实例
     * @return 实例
     */
    public static EntityManager getInstance() {
        if(Instance == null)
            Instance = new EntityManager();
        return Instance;
    }

    /**
     * 储存目前存在的所有实体
     */
    private List<Entity> entityList;

    /**
     * 实体注册信息
     */
    private HashMap<Integer, EntityRegisterMessage> entityRegisterMessages;

    /**
     * 实体信息
     */
    private HashMap<Integer, EntityMessage> entityMessageHashMap;

    /**
     * 实体修改锁
     */
    private final Object entityListModifyLock = new Object();

    /**
     * 实体编号计数器
     */
    private int entityCount;

    /**
     * 初始化实体管理类
     */
    private EntityManager() {
        synchronized (entityListModifyLock) {
            entityList = new ArrayList<>();
            entityRegisterMessages = new HashMap<>();
            entityMessageHashMap = new HashMap<>();
        }
        entityCount = 0;
    }

    /**
     * 让所有实体进行一帧运动（移动、碰撞、伤害、死亡判断）
     */
    public void run() {

        if(entityList.size() == 0) {
            System.out.println("实体列表为空");
            return;
        }

        // 第一阶段，实体刷新（控制器）+ 前景hp刷新
        for(int i = 0; i < entityList.size(); i++) {
            if(entityList.get(i) instanceof Character) {
                ((Character)entityList.get(i)).play();
            }
        }

        boolean havePlayedHitSound = false;
        boolean havePlayedHitWallSound = false;

        // 第二阶段，以移动为核心的判断（移动、碰撞、伤害）
        for(int i = 0; i < entityList.size(); i++) {
            Entity entity = entityList.get(i);

            if(entity instanceof Obstacle) continue; // 障碍物不会移动
            if(entity.getHitbox().getVx() == 0 && entity.getHitbox().getVy() == 0) continue; // 静止实体不会移动

            Hitbox hitbox = entity.getHitbox();
            
            // 移动↓
            Hitbox nextHitbox = entity.getHitbox().nextFrameHitbox(); // "true" next frame hitbox
            for(int j = 0; j < entityList.size(); j++) {
                if(i == j) continue; // 同个物体，跳过

                Entity anotherEntity = entityList.get(j);
                //if(!entityList.get(j).isExist()) // 实体不存在，跳过
                if(!anotherEntity.getHitbox().isHit(nextHitbox)) continue; // 没碰撞，跳过

                if(entity instanceof Projectile && anotherEntity instanceof Character && ((Projectile)entity).getSenderID() == (anotherEntity).getId()) continue;
                if(anotherEntity instanceof Projectile && entity instanceof Character && ((Projectile)anotherEntity).getSenderID() == (entity).getId()) continue;

                Hitbox anotherHitbox = anotherEntity.getHitbox();

                // 发生碰撞，处理碰撞
                // 1. 位置(碰撞)处理 - 改变了nextHitbox
                if(!(anotherEntity instanceof Projectile)) {
                    // 1. vx == 0, 调整y
                    // 2. vy == 0, 调整x
                    // 3. 回退x，如果仍然碰撞，x自由，调整y
                    // 4. 回退y，如果仍然碰撞，y自由，调整x
                    // 5. 回退x和y后，两者均不碰撞，则调整空余距离多的那一个轴。
                    char cood;
                    if(nextHitbox.getVx() == 0) { // 1
                        cood = 'y';
                    } else if(nextHitbox.getVy() == 0) { // 2
                        cood = 'x';
                    } else if(anotherHitbox.isHit(new Hitbox(hitbox.getX(), nextHitbox.getY(), nextHitbox.getLx(), nextHitbox.getLy()))) { // 3
                        // 注意这里的判断，新hitbox的x轴是移动之前hitbox的x轴
                        cood = 'y';
                    } else if(anotherHitbox.isHit(new Hitbox(nextHitbox.getX(), hitbox.getY(), nextHitbox.getLx(), nextHitbox.getLy()))) {
                        cood = 'x';
                    } else {
                        int disX = Math.abs(hitbox.getX() - anotherHitbox.getX()) - hitbox.getLx() - anotherHitbox.getLx();
                        int disY = Math.abs(hitbox.getY() - anotherHitbox.getY()) - hitbox.getLy() - anotherHitbox.getLy();
                        if(disX > disY) cood = 'x';
                        else cood = 'y';
                    }
                    // 调整x轴或调整y轴
                    if(cood == 'x') {
                        if(nextHitbox.getVx() > 0)
                            nextHitbox.setCoordinate(anotherHitbox.getX() - anotherHitbox.getLx() - nextHitbox.getLx(), nextHitbox.getY());
                        else
                            nextHitbox.setCoordinate(anotherHitbox.getX() + anotherHitbox.getLx() + nextHitbox.getLx(), nextHitbox.getY());
                    } else {
                        if(nextHitbox.getVy() > 0)
                            nextHitbox.setCoordinate(nextHitbox.getX(), anotherHitbox.getY() - anotherHitbox.getLy() - nextHitbox.getLy());
                        else
                            nextHitbox.setCoordinate(nextHitbox.getX(), anotherHitbox.getY() + anotherHitbox.getLy() + nextHitbox.getLy());
                    }
                }

                // 2. 伤害处理
                if(entity instanceof Projectile) {
                    anotherEntity.reduceHp(((Projectile)entity).getDamage());
                    entity.reduceHp(1);
                    if((anotherEntity instanceof Character) && !havePlayedHitSound) {
                        havePlayedHitSound = true;
                        // MyTool.playSound("sound/hit.wav");
                        // entityRegisterMessages.add(new HitSoundEffectMessage(0));
                    } else if(anotherEntity instanceof Obstacle) {
                        // MyTool.playSound("sound/blt_imp_masonry_far_01.wav");
                        // entityRegisterMessages.add(new HitSoundEffectMessage(1));
                    }
                } else if(anotherEntity instanceof Projectile) {
                    entity.reduceHp(((Projectile)anotherEntity).getDamage());
                    anotherEntity.reduceHp(1);
                    if((entity instanceof Character) && !havePlayedHitSound) {
                        havePlayedHitSound = true;
                        // MyTool.playSound("sound/hit.wav");
                        // entityRegisterMessages.add(new HitSoundEffectMessage(0));
                    }
                }

                
            }

            if (entity instanceof Projectile) {
                int xo = ((Projectile)entity).getOx();
                int yo = ((Projectile)entity).getOy();
                int x = hitbox.getX();
                int y = hitbox.getY();
                int range = ((Projectile)entity).getRange();
                if (Math.abs(x - xo) * Math.abs(x - xo) + Math.abs(y - yo) * Math.abs(y - yo) >= range * range) {
                    entity.reduceHp(10000);
                }
            }

            // 更新第i个实体的位置
            hitbox.setCoordinate(nextHitbox.getX(), nextHitbox.getY());
        }

        
        // 记录实体信息
        for(Entity e: entityList) {
            EntityMessage entityMessage = new EntityMessage(e.getId(), e.getHp(), e.getHitbox().getX(), e.getHitbox().getY(), e.getHitbox().getVx(), e.getHitbox().getVy(), e.getHitbox().getLx(), e.getHitbox().getLy());
            if (e instanceof Character) {
                Character character = (Character)e;
                entityMessage.setAimX(character.getAimX());
                entityMessage.setAimY(character.getAimY());
                if (character.getWeapon() != null) {
                    entityMessage.setWeaponType(character.getWeapon().getType());
                }
            }
            
            entityMessageHashMap.put(e.getId(), entityMessage);
        }

        // 第三阶段，将死亡(hp<=0)的实体从entityList中移除
        clearDeadEntity();
    }

    private void clearDeadEntity() {
        synchronized (entityListModifyLock) {
            ArrayList<Entity> newEntityList = new ArrayList<>();
            for(Entity e: entityList) {
                if(!e.isExist()) { // 死亡
                    entityMessageHashMap.remove(e.getId());
                    entityRegisterMessages.remove(e.getId());
                    if(e instanceof Character) {
                        // TODO 播放死亡特效
                    }
                } else {
                    newEntityList.add(e);

                }
            }
            entityList = newEntityList;
        }
    }

    /**
     * 重置实体管理器
     *  - 清空实体列表
     */
    public void restart() {
        synchronized (entityListModifyLock) {
            entityList = new ArrayList<>();
        }
        entityCount = 0;
    }

    /**
     * 往游戏中添加一个新实体
     * @param entity 新实体
     * @param entityRegisterMessage 实体信息
    */
    public void add(Entity entity, EntityRegisterMessage entityRegisterMessage) {
        if(entity == null) {
            System.out.println("添加了空实体");
            return;
        }
        
        entity.setId(++entityCount);
        synchronized (entityListModifyLock) {
            entityList.add(entity);
        }
        entityRegisterMessage.setId(entityCount);
        log.info("add entity: {}", entityRegisterMessage.getId());
        entityRegisterMessages.put(entityRegisterMessage.getId(), entityRegisterMessage);
    }
    

    /**
     * 往游戏中添加一个新实体
     * @param entity 新实体
     */
    public void addWithoutMessage(int ID, Entity entity) {
        if(entity == null) {
            System.out.println("添加了空实体");
            return;
        }
        entity.setId(ID);
        log.info("add entity: {}", entity.getId());
        synchronized (entityListModifyLock) {
            entityList.add(entity);
        }
    }

    /**
     * 往游戏中添加一个新实体
     * @param entity 新实体
     */
    public void addWithoutMessage(Entity entity) {
        if(entity == null) {
            System.out.println("添加了空实体");
            return;
        }
        entity.setId(++entityCount);
        synchronized (entityListModifyLock) {
            entityList.add(entity);
        }
    }

    /**
     * 按给定方式排序实体列表
     * @param comparator 比较器
     */
    public void sort(Comparator<Entity> comparator) {
        synchronized (entityListModifyLock) {
            entityList.sort(comparator);
        }
    }

    /**
     * 获取实体列表中的第index个实体
     * @param index 编号（从0开始）
     * @return 实体
     */
    public Entity getEntity(int index) {
        return entityList.get(index);
    }

    /**
     * 获取实体列表的大小
     * @return 大小
     */
    public int getEntitySize() {
        return entityList.size();
    }

    /**
     * 获取实体管理器信息
     * @return 实体管理器信息
     */
    public EntityManagerMessage getEntityManagerMessage() {
        EntityManagerMessage entityManagerMessage = new EntityManagerMessage(entityRegisterMessages, entityMessageHashMap);
        // 清空实体注册信息
        // entityRegisterMessages = new ArrayList<>();
        return entityManagerMessage;
    }

    /**
     * 加载实体管理器信息
     * @param entityManagerMessage 实体管理器信息
     */
    public void loadEntityManagerMessage(EntityManagerMessage entityManagerMessage) {
        
        for (EntityRegisterMessage entityRegisterMessage: entityManagerMessage.getEntityRegisterMessages().values()) {
            if (entityMessageHashMap.get(entityRegisterMessage.getId()) != null) continue;
            if (entityRegisterMessage instanceof CharacterRegisterMessage) {
                CharacterRegisterMessage characterRegisterMessage = (CharacterRegisterMessage)entityRegisterMessage;
                Character character = CharacterFactory.getCharacter(characterRegisterMessage.getUserId(), characterRegisterMessage.getWeaponType(), null);
                addWithoutMessage(characterRegisterMessage.getId(), character);
            } else if (entityRegisterMessage instanceof ObstacleRegisterMessage) {
                ObstacleRegisterMessage obstacleRegisterMessage = (ObstacleRegisterMessage)entityRegisterMessage;
                Obstacle obstacle = ObstacleFactory.getObstacle(obstacleRegisterMessage.getType(), 0, 0);
                addWithoutMessage(obstacleRegisterMessage.getId(), obstacle);
            } else if (entityRegisterMessage instanceof ProjectileRegisterMessage) {
                ProjectileRegisterMessage projectileRegisterMessage = (ProjectileRegisterMessage)entityRegisterMessage;
                Projectile projectile = ProjectileFactory.getProjectile(projectileRegisterMessage.getType(), projectileRegisterMessage.getSenderId(), projectileRegisterMessage.getOx(), projectileRegisterMessage.getOy());
                addWithoutMessage(projectileRegisterMessage.getId(), projectile);
            }
        }
        this.entityMessageHashMap = entityManagerMessage.getEntityMessageHashMap();
        for(int i = 0; i < entityList.size(); i++) {
            Entity entity = entityList.get(i);
            EntityMessage entityMessage = entityMessageHashMap.get(entity.getId());
            // log.info("load entity: {}", entity.getId());
            if(entityMessage == null) continue;
            entity.setHp(entityMessage.getHp());
            entity.getHitbox().setCoordinate(entityMessage.getX(), entityMessage.getY());
            entity.getHitbox().setVelocity(entityMessage.getVx(), entityMessage.getVy());
            entity.getHitbox().setHitboxLength(entityMessage.getLx(), entityMessage.getLy());
            if (entity instanceof Character) {
                Character character = (Character)entity;
                character.aim(entityMessage.getAimX(), entityMessage.getAimY());
                if (entityMessage.getWeaponType() != 0) {
                    if (character.getWeapon() == null || character.getWeapon().getType() != entityMessage.getWeaponType()) {
                        character.setWeapon(WeaponFactory.getWeapon(entityMessage.getWeaponType()));
                    }
                }
            }
        }
    }


}
