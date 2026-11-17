export const html = htm.bind(React.createElement);
export const { useState, useEffect, useRef } = React;

export function formatDate(value) {
    if (!value) return '';
    const date = new Date(String(value).replace(/\[.*\]$/, ''));
    return isNaN(date) ? String(value) : date.toLocaleString('ru-RU');
}
