// Описание сущностей: колонки таблицы, строковые колонки для фильтра/сортировки и поля формы.
// Колонка: [заголовок, значение, тип связанной сущности (если это ссылка)].

export const ENUMS = {
    Color: ['GREEN', 'RED', 'BLACK', 'YELLOW', 'WHITE'],
    Country: ['GERMANY', 'INDIA', 'NORTH_KOREA'],
    TicketType: ['VIP', 'USUAL', 'BUDGETARY'],
    VenueType: ['BAR', 'LOFT', 'OPEN_AREA', 'MALL'],
};

const nameLabel = (item) => `#${item.id} ${item.name}`;

export const ENTITIES = {
    tickets: {
        title: 'Билеты',
        one: 'билет',
        label: nameLabel,
        textColumns: { name: 'Название', type: 'Тип' },
        columns: [
            ['ID', (t) => t.id],
            ['Название', (t) => t.name],
            ['Координаты', (t) => `(${t.coordinates.x}; ${t.coordinates.y})`],
            ['Создан', (t) => t.creationDate, 'date'],
            ['Человек', (t) => t.person, 'persons'],
            ['Мероприятие', (t) => t.event, 'events'],
            ['Цена', (t) => t.price],
            ['Тип', (t) => t.type],
            ['Скидка', (t) => t.discount],
            ['Номер', (t) => t.number],
            ['Площадка', (t) => t.venue, 'venues'],
        ],
        fields: [
            { name: 'name', label: 'Название', required: true, maxLength: 100 },
            { name: 'coordinates.x', label: 'Координата X (больше -626)', type: 'int', required: true, min: -625 },
            { name: 'coordinates.y', label: 'Координата Y', type: 'number', required: true },
            { name: 'price', label: 'Цена (больше 0)', type: 'number', required: true, positive: true },
            { name: 'type', label: 'Тип', enum: 'TicketType' },
            { name: 'discount', label: 'Скидка (1–100)', type: 'int', required: true, min: 1, max: 100 },
            { name: 'number', label: 'Номер (больше 0)', type: 'number', positive: true },
            { name: 'personId', label: 'Человек', ref: 'persons', from: 'person' },
            { name: 'eventId', label: 'Мероприятие', ref: 'events', from: 'event' },
            { name: 'venueId', label: 'Площадка', ref: 'venues', from: 'venue' },
        ],
    },
    persons: {
        title: 'Люди',
        one: 'человек',
        label: (p) => `#${p.id}` + (p.nationality ? ` ${p.nationality}` : ''),
        textColumns: { eyeColor: 'Цвет глаз', hairColor: 'Цвет волос', nationality: 'Национальность' },
        columns: [
            ['ID', (p) => p.id],
            ['Цвет глаз', (p) => p.eyeColor],
            ['Цвет волос', (p) => p.hairColor],
            ['Локация', (p) => p.location, 'locations'],
            ['Рост', (p) => p.height],
            ['Национальность', (p) => p.nationality],
        ],
        fields: [
            { name: 'eyeColor', label: 'Цвет глаз', enum: 'Color' },
            { name: 'hairColor', label: 'Цвет волос', enum: 'Color' },
            { name: 'locationId', label: 'Локация', ref: 'locations', from: 'location' },
            { name: 'height', label: 'Рост (больше 0)', type: 'number', positive: true },
            { name: 'nationality', label: 'Национальность', enum: 'Country' },
        ],
    },
    events: {
        title: 'Мероприятия',
        one: 'мероприятие',
        label: nameLabel,
        textColumns: { name: 'Название' },
        columns: [
            ['ID', (e) => e.id],
            ['Название', (e) => e.name],
            ['Дата', (e) => e.date, 'date'],
            ['Мин. возраст', (e) => e.minAge],
        ],
        fields: [
            { name: 'name', label: 'Название', required: true, maxLength: 100 },
            { name: 'date', label: 'Дата', type: 'datetime', required: true },
            { name: 'minAge', label: 'Минимальный возраст', type: 'int', required: true },
        ],
    },
    venues: {
        title: 'Площадки',
        one: 'площадка',
        label: nameLabel,
        textColumns: { name: 'Название', type: 'Тип' },
        columns: [
            ['ID', (v) => v.id],
            ['Название', (v) => v.name],
            ['Вместимость', (v) => v.capacity],
            ['Тип', (v) => v.type],
        ],
        fields: [
            { name: 'name', label: 'Название', required: true, maxLength: 100 },
            { name: 'capacity', label: 'Вместимость (больше 0)', type: 'int', min: 1 },
            { name: 'type', label: 'Тип', enum: 'VenueType' },
        ],
    },
    locations: {
        title: 'Локации',
        one: 'локация',
        label: nameLabel,
        textColumns: { name: 'Название' },
        columns: [
            ['ID', (l) => l.id],
            ['X', (l) => l.x],
            ['Y', (l) => l.y],
            ['Z', (l) => l.z],
            ['Название', (l) => l.name],
        ],
        fields: [
            { name: 'x', label: 'X', type: 'int', required: true },
            { name: 'y', label: 'Y', type: 'int', required: true },
            { name: 'z', label: 'Z', type: 'number', required: true },
            { name: 'name', label: 'Название', required: true, maxLength: 255 },
        ],
    },
};

/** Подписи полей в окне просмотра. */
export const LABELS = {
    id: 'ID', name: 'Название', coordinates: 'Координаты', x: 'X', y: 'Y', z: 'Z',
    creationDate: 'Создан', person: 'Человек', event: 'Мероприятие', venue: 'Площадка',
    location: 'Локация', price: 'Цена', type: 'Тип', discount: 'Скидка', number: 'Номер',
    eyeColor: 'Цвет глаз', hairColor: 'Цвет волос', height: 'Рост', nationality: 'Национальность',
    date: 'Дата', minAge: 'Мин. возраст', capacity: 'Вместимость',
};
