package ru.gx.core.data;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Интерфейс InMemory-репозитория объектов наследников DataObjects
 * @param <O> тип Data Objects, которые обрабатывает данные репозиторий
 */
@SuppressWarnings("unused")
public interface DataMemoryRepository<O extends DataObject, P extends DataPackage<O>>
        extends Iterable<O>, DataObjectKeyExtractor<O> {
    /**
     * @return Количество объектов в Репозитории.
     */
    int size();

    /**
     * @return Список всех объектов.
     */
    Iterable<O> getAll();

    /**
     * Запись объекта object с ключом key в репозиторий.
     * @param object    Объект.
     * @return          Предыдущий объект с заданным ключом, если такой был.
     */
    @SuppressWarnings("UnusedReturnValue")
    @Nullable
    O put(@NotNull O object);

    /**
     * Запись нескольких объектов с соответствующими ключами для них.
     * @param source    Map-а ключей и объектов.
     */
    void putAll(@NotNull Collection<O> source);

    /**
     * Добавление объекта в репозиторий.
     * @param object                            Добавляемый объект.
     * @throws ObjectAlreadyExistsException     Ошибка, если для ключа key уже зарегистрирован объект в репозитории.
     */
    void insert(@NotNull O object) throws ObjectAlreadyExistsException;

    /**
     * Обновление объекта с ключом key. Обновляемый экземпляр не заменяется, а обновляются данные самого объекта.
     * @param object                        Новое состояние объекта.
     * @throws JsonMappingException         Ошибка при десериализации объекта в объект.
     * @throws ObjectNotExistsException     Ошибка, если для ключа key не зарегистрирован объект в репозитории.
     */
    void update(@NotNull O object) throws JsonMappingException, ObjectNotExistsException;

    /**
     * Замена объекта с ключом key в репозитории.
     * @param object                        Новый объект, который заменит старый объект.
     * @return                              Предыдущий объект, который был ассоциирован с ключом key.
     * @throws ObjectNotExistsException     Ошибка в случае, если объекта с таким ключом в Репозитории не зарегистрировано.
     */
    @NotNull
    O replace(@NotNull O object) throws ObjectNotExistsException;

    /**
     * Удаление объекта из репозитория, который зарегистрирован для ключа key.
     * @param key       Ключ.
     * @return          Объект, если
     */
    @Nullable
    O removeByKey(@NotNull Object key);

    /**
     * Удаление объекта object из репозитория, который зарегистрирован для ключа key.
     * @param object        Удаляемый объект.
     * @return              Удаленный объект, если с заданным ключом был объект, указанный в параметре object.
     */
    @Nullable
    O remove(@NotNull O object);

    /**
     * Получение объекта по идентификатору (ключу), который указан у класса в @JsonIdentityInfo.
     * @param key значение ключа, по которому ищем объект.
     * @return объект, если такой найден; null, если по такому ключу в IdResolver-е нет объекта.
     */
    @SuppressWarnings("unused")
    @Nullable
    O getByKey(@NotNull final Object key);

    /**
     * Проверка наличия объекта с указанным ключом в репозитории.
     * @param key Ключ.
     * @return true - объект есть, false - объекта нет.
     */
    @SuppressWarnings("unused")
    boolean containsKey(@NotNull final Object key);

    /**
     * Получение ключа объекта, по которому его идентифицирует данный MemoryRepository.
     * @param dataObject    Объект данных, из которого "извлекаем" ключ.
     * @return              Ключ, идентифицирующий указанный объект данных.
     */
    @NotNull
    Object extractKey(@NotNull final O dataObject);
}
